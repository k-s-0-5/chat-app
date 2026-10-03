export async function fetchConversation(accountIds) {
  const response = await fetch(`/conversation`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(accountIds),
  });
  checkAndThrowError(response, "Failed to create conversation");
  return response.text();
}

export async function fetchOlderMessages(
  conversationId,
  oldestMessageTimestamp,
  oldestMessageId,
) {
  const response = await fetch(
    `/conversation/${conversationId}/messages?before=${encodeURIComponent(oldestMessageTimestamp)}&beforeid=${encodeURIComponent(oldestMessageId)}`,
    {
      headers: { "X-Requested-With": "XMLHttpRequest" },
    },
  );
  checkAndThrowError(response, "Failed to fetch older messages");
  return response.text();
}

export async function fetchMessages(conversationId, oldestMessageTimestamp) {
  const response = await fetch(
    `/conversation/${conversationId}/messages?before=${encodeURIComponent(oldestMessageTimestamp)}`,
    {
      headers: { "X-Requested-With": "XMLHttpRequest" },
    },
  );
  checkAndThrowError(response, "Failed to fetch messages");
  return response.text();
}

export async function fetchUserId() {
  const response = await fetch("/accounts/me");
  checkAndThrowError(response, "Failed to fetch user");
  return response.json();
}

export async function fetchAccounts(usernameSegment) {
  const response = await fetch(
    `/accounts/search?username=${encodeURIComponent(usernameSegment)}`,
  );
  checkAndThrowError(response, "Failed to fetch accounts");
  return response.json();
}

function checkAndThrowError(response, errorMessage) {
  if (!response.ok) {
    throw new Error(errorMessage);
  }
}
