import {
  fetchConversation,
  fetchMessages,
  fetchOlderMessages,
  fetchUserId,
  fetchAccounts,
} from "/js/api.js";
import {
  buildHTML,
  buildMessage,
  buildAccountSearchResults,
  clearSearchResults,
  hideElement,
  moveToFirst,
} from "/js/render.js";
import {
  getEncodedText,
  getJavaTime,
  shouldSendOnKeydown,
} from "/js/utilities.js";

// ==================== VARIABLES ====================
let conversationId = null;
let selectedElement = null;
let subscriptions = {};
const stompClient = new StompJs.Client({
  brokerURL: "ws://localhost:8080/connect",
  onConnect: (frame) => {
    subscribeToConversation();
    subscribeToChat();
  },
  reconnectDelay: 5000,
  heartbeatIncoming: 3000,
  heartbeatOutgoing: 3000,
});
let userId = null;
let oldestMessageTimestamp = null;
let oldestMessageId = null;
let hasOlderMessages = true;
let loading = false;

// ==================== DOM ELEMENTS ====================
const searchContainer = document.getElementById("search-container");
const searchInput = document.getElementById("search-input");
const searchResults = document.getElementById("search-results");
const searchButton = document.getElementById("search-button");
const messageList = document.getElementById("message-list");
const chatScroll = document.getElementById("chat-scroll");
const loadingDiv = document.getElementById("loading");
const messageInput = document.getElementById("message-input");
const messageButton = document.getElementById("message-button");
const conversationList = document.getElementById("conversation-list");
const messageError = document.getElementById("message-error");

// ==================== LISTENERS ====================
const loadPage = async (event) => {
  userId = await getUserId();
  const conversation = window.location.pathname.match(
    /\/conversation\/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})\/messages/,
  );
  if (conversation) {
    getMessages(document.querySelector(`[data-id="${conversation[1]}"]`));
  }
};

const closeSearch = async (event) => {
  if (!searchContainer.contains(event.target)) {
    clearSearchResults(searchResults, searchInput);
  }
};

const scroll = async (event) => {
  if (chatScroll.scrollTop == 0 && !loading && hasOlderMessages) {
    loading = true;
    const scrollHeight = chatScroll.scrollHeight;
    loadingDiv.hidden = false;
    await getOlderMessages();
    loadingDiv.hidden = true;
    chatScroll.scrollTop = chatScroll.scrollHeight - scrollHeight;
    scrollHeight = chatScroll.scrollHeight;
    loading = false;
  }
};

const searchForAccounts = async (event) => {
  if (!shouldSendOnKeydown(event)) {
    return;
  }
  try {
    const accountsJSON = await fetchAccounts(searchInput.value);
    if (accountsJSON.length == 0) {
      console.log("No accounts found");
      return;
    }
    buildAccountSearchResults(accountsJSON, searchResults, searchInput);
  } catch (error) {
    console.error(error);
  }
};

const sendMessage = (event) => {
  if (
    !messageInput.value ||
    !subscriptions.chat ||
    !shouldSendOnKeydown(event)
  ) {
    return;
  }
  if (getEncodedText(messageInput.value).length > 1000) {
    messageError.hidden = false;
    setTimeout(() => hideElement(messageError), 5000);
    return;
  }
  const message = {
    conversationId: conversationId,
    contents: messageInput.value,
  };
  stompClient.publish({
    destination: "/ws/messages.send",
    body: JSON.stringify(message),
  });
  messageInput.value = "";
};

function getClickListener(selector, callback) {
  return (event) => {
    const element = event.target.closest(selector);
    if (element) {
      callback(element);
    }
  };
}

// ==================== FUNCTIONS ====================
init();
function init() {
  document.addEventListener("DOMContentLoaded", loadPage);
  document.addEventListener("click", closeSearch);
  chatScroll.addEventListener("scroll", scroll);
  searchButton.addEventListener("click", searchForAccounts);
  searchInput.addEventListener("keydown", searchForAccounts);
  messageInput.addEventListener("keydown", sendMessage);
  messageButton.addEventListener("click", sendMessage);
  searchResults.addEventListener(
    "click",
    getClickListener("[data-id]", createConversation),
  );
  conversationList.addEventListener(
    "click",
    getClickListener("[data-id]", getMessages),
  );
  messageList.addEventListener(
    "click",
    getClickListener("button.edit-message", editMessage),
  );
}

async function getMessages(element) {
  if (element.getAttribute("data-id") == conversationId) {
    return;
  }
  if (conversationId != null) {
    selectedElement.classList.remove("selected-box");
  }
  if (subscriptions.chat) {
    subscriptions.chat.unsubscribe();
  }
  selectedElement = element;
  element.classList.add("selected-box");
  conversationId = element.getAttribute("data-id");
  oldestMessageTimestamp = getJavaTime(Date.now());
  hasOlderMessages = true;

  try {
    const cid = conversationId;
    const html = await fetchMessages(conversationId, oldestMessageTimestamp);
    if (cid === conversationId) {
      // User has not changed conversation
      messageList.innerHTML = html;
      if (messageList.querySelector(".message-wrapper")) {
        oldestMessageTimestamp =
          messageList.querySelector(".message-wrapper").dataset.sentAt;
        oldestMessageId =
          messageList.querySelector(".message-wrapper").dataset.id;
      }
      const scrollHeight = chatScroll.scrollHeight;
      chatScroll.scrollTop = scrollHeight;
      history.pushState(
        { id: conversationId },
        "",
        `/conversation/${cid}/messages`,
      );
    } else {
      console.log("Conversation changed");
    }
  } catch (error) {
    console.error(error);
  }
  subscribeToChat();
}

function subscribeToConversation() {
  if (!stompClient.connected || !userId) {
    return;
  }
  if (subscriptions.conversation) {
    subscriptions.conversation.unsubscribe();
  }
  subscriptions.conversation = stompClient.subscribe(
    "/topic/" + userId,
    function (message) {
      const parsedMessage = JSON.parse(message.body);
      const element = conversationList.querySelector(
        `[data-id="${parsedMessage.body}"]`,
      );
      if (element) {
        moveToFirst(conversationList, element);
      }
    },
  );
}

function subscribeToChat() {
  if (!stompClient.connected || !conversationId) {
    return;
  }
  if (subscriptions.chat) {
    subscriptions.chat.unsubscribe();
  }
  subscriptions.chat = stompClient.subscribe(
    "/topic/messages." + conversationId,
    function (message) {
      const parsedMessage = JSON.parse(message.body);
      if (parsedMessage.event == "send") {
        messageList.appendChild(buildMessage(parsedMessage, userId));
        scrollHeight = chatScroll.scrollHeight;
        chatScroll.scrollTop = scrollHeight;
      } else if (parsedMessage.event == "edit") {
        document
          .querySelector(`[data-id="${parsedMessage.body.id}"]`)
          .replaceWith(buildMessage(parsedMessage, userId));
      } else if (parsedMessage.event == "delete") {
        document.querySelector(`[data-id="${parsedMessage.body}"]`).remove();
      }
    },
  );
}

async function getOlderMessages() {
  if (!hasOlderMessages) return;
  try {
    const cid = conversationId;
    const html = await fetchOlderMessages(
      conversationId,
      oldestMessageTimestamp,
      oldestMessageId,
    );
    if (cid == conversationId) {
      // User has not changed conversation
      const parsed = new DOMParser().parseFromString(html, "text/html");
      if (!parsed.querySelector(".message-wrapper")) {
        hasOlderMessages = false;
        return;
      }
      messageList.insertAdjacentHTML("afterbegin", html);
      oldestMessageTimestamp =
        messageList.querySelector(".message-wrapper").dataset.sentAt;
      oldestMessageId =
        messageList.querySelector(".message-wrapper").dataset.id;
    } else {
      console.log("Conversation changed");
    }
  } catch (error) {
    console.error(error);
  }
}

function editMessage(element) {
  const currentMessage = element.closest(".message--out");
  const childElements = [...currentMessage.children];
  const messageId = currentMessage.closest(".message-wrapper").dataset.id;
  const button = currentMessage.querySelector("button");
  const html = buildHTML("input", {
    type: "text",
    id: "message",
    value: currentMessage.querySelector("p").textContent,
  });
  const submitMessageUpdate = (event) => {
    if (!shouldSendOnKeydown(event)) {
      return;
    }
    if (!html.value) {
      // Delete clause
      const message = messageId;
      stompClient.publish({
        destination: "/ws/messages.delete",
        body: JSON.stringify(message),
      });
    } else {
      // Update clause
      const message = {
        id: messageId,
        contents: html.value,
      };
      stompClient.publish({
        destination: "/ws/messages.edit",
        body: JSON.stringify(message),
      });
    }
    document.removeEventListener("submit", submitMessageUpdate);
  };
  html.addEventListener("keydown", submitMessageUpdate);
  const restoreMessage = (event) => {
    if (!html.contains(event.target) && !button.contains(event.target)) {
      currentMessage.replaceChildren(...childElements);
      document.removeEventListener("click", restoreMessage);
    }
  };
  document.addEventListener("click", restoreMessage);
  currentMessage.replaceChildren(html);
}

async function createConversation(element) {
  const id = element.dataset.id;
  try {
    const accountIds = [id];
    const html = await fetchConversation(accountIds);
    conversationList.innerHTML = html;
    selectedElement = conversationList.querySelector(
      `[data-id="${conversationId}"]`,
    );
    if (selectedElement) {
      selectedElement.classList.add("selected-box");
    }
    clearSearchResults(searchResults, searchInput);
  } catch (error) {
    console.error(error);
  }
}

async function getUserId() {
  try {
    const id = await fetchUserId();
    stompClient.activate();
    return id;
  } catch (error) {
    console.error(error);
    return null;
  }
}
