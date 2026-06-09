CREATE TABLE IF NOT EXISTS Account (
    id UUID PRIMARY KEY,
    username VARCHAR(25) NOT NULL UNIQUE,
    email VARCHAR(254) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS Conversation (
    id UUID PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    lastSent timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS Message (
    id BIGINT PRIMARY KEY,
    account_id UUID NOT NULL,
    conversation_id UUID NOT NULL,
    sent_at timestamp NOT NULL,
    contents varchar(250) NOT NULL,
    attachment_url varchar(100),
    is_read BOOLEAN,
    edited BOOLEAN,

    FOREIGN KEY (account_id) REFERENCES Account(id) ON DELETE CASCADE,
    FOREIGN KEY (conversation_id) REFERENCES Conversation(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Participant (
    id INT PRIMARY KEY AUTO_INCREMENT, 
    account_id UUID NOT NULL,
    conversation_id UUID NOT NULL,
    role VARCHAR(16),

    FOREIGN KEY (account_id) REFERENCES Account(id) ON DELETE CASCADE,
    FOREIGN KEY (conversation_id) REFERENCES Conversation(id) ON DELETE CASCADE,
    UNIQUE (account_id, conversation_id)
);