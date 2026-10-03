import { getFormattedTime, editIcon } from "/js/utilities.js";

export function buildHTML(tag, attributes, ...children) {
  const node = document.createElement(tag);
  if (attributes) {
    for (const [key, value] of Object.entries(attributes)) {
      if (key == "style") {
        node.style.cssText = value;
      } else {
        if (key.includes("-")) {
          node.setAttribute(key, value);
        } else {
          node[key] = value;
        }
      }
    }
  }

  children.forEach((child) => {
    node.append(child);
  });

  return node;
}

export function createText(text) {
  return document.createTextNode(text);
}

export function buildMessage(parsedMessage, userId) {
  const isMe = parsedMessage.body.accountId == userId;
  const button = buildHTML("button", { className: "edit-message" });
  button.innerHTML = editIcon;
  const message = buildHTML(
    "div",
    {
      className: "message-wrapper",
      "data-id": parsedMessage.body.id,
      "data-sent-at": parsedMessage.body.sentAt,
    },
    buildHTML(
      "div",
      { className: `time ${isMe ? "time--out" : "time--in"}` },
      buildHTML(
        "small",
        {},
        createText(
          getFormattedTime(parsedMessage.body.sentAt) +
            (parsedMessage.body.edited ? " edited" : ""),
        ),
      ),
    ),
    buildHTML(
      "div",
      { className: `message ${isMe ? "message--out" : "message--in"}` },
      ...(isMe
        ? [
            button,
            buildHTML("p", null, createText(parsedMessage.body.contents)),
          ]
        : [buildHTML("p", null, createText(parsedMessage.body.contents))]),
    ),
  );
  return message;
}

export function buildAccountSearchResults(
  accountsJSON,
  searchResults,
  searchInput,
) {
  clearSearchResults(searchResults, searchInput);
  searchInput.style.borderRadius = "7px 7px 0px 0px";
  for (let i = 0; i < accountsJSON.length; i++) {
    let style =
      i == accountsJSON.length - 1 ? "result result--final" : "result"; // Pick appropriate styling
    const result = buildHTML(
      "button",
      { className: style, "data-id": accountsJSON[i].id },
      createText(accountsJSON[i].username),
    );
    searchResults.appendChild(result);
  }
}

export function clearSearchResults(searchResults, searchInput) {
  searchResults.replaceChildren();
  searchInput.style.borderRadius = "7px";
}

export function hideElement(element) {
  element.hidden = true;
}

export function swapElements(element1, element2) {
  const temp = document.createElement("div");
  element1.before(temp);
  element2.before(element1);
  temp.replaceWith(element2);
}

export function moveToFirst(parent, element) {
  parent.prepend(element);
}
