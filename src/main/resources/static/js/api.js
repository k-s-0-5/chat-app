export async function fetchConversation(accountIds) {
  const response = await fetch(`/conversation`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(accountIds),
  });

  if (!response.ok) {
    throw new Error(`Failed to create conversation`);
  }

  return response.text();
}

export async function fetchOlderMessages(
  conversationId,
  oldestMessageTimestamp,
  oldestMessageId,
) {
  const response = await fetch(
    `/conversation/
      ${conversationId}/messages?before=${encodeURIComponent(oldestMessageTimestamp)}&beforeid=${encodeURIComponent(oldestMessageId)}`,
    {
      headers: { "X-Requested-With": "XMLHttpRequest" },
    },
  );

  if (!response.ok) {
    throw new Error(`Failed to fetch older messages`);
  }

  return response.text();
}

export async function fetchMessages(
  conversationId,
  oldestMessageTimestamp,
) {
  const response = await fetch(
    `/conversation/${conversationId}/messages?before=${encodeURIComponent(oldestMessageTimestamp)}`,
    {
      headers: { "X-Requested-With": "XMLHttpRequest" },
    },
  );

  if (!response.ok) {
    throw new Error(`Failed to fetch messages`);
  }

  return response.text();
}

export async function fetchUserId() {
  const response = await fetch("/accounts/me");

  if (!response.ok) {
    throw new Error(`Failed to fetch user`);
  }

  return response.json();
}

export async function fetchAccounts(usernameSegment) {
  const response = await fetch(`/accounts/search?username=${encodeURIComponent(usernameSegment)}`);

  if (!response.ok) {
    throw new Error(`Failed to fetch accounts`);
  }

  return response.json();
}
