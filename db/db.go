package db

import (
	"database/sql"
	"fmt"
	"log"
	"time"

	"github.com/adezxc/messenger-discord-bot/types"
	_ "github.com/mattn/go-sqlite3"
)

type DB struct {
	*sql.DB
}

func CreateDb(dbName string) error {
	db, err := sql.Open("sqlite3", fmt.Sprintf(dbName))
	if err != nil {
		return err
	}
	defer db.Close()

	memberTableStmt := `
	CREATE TABLE "members" (
    memberId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
    name TEXT UNIQUE
  );
	`

	_, err = db.Exec(memberTableStmt)
	if err != nil {
		log.Printf("%q: %s\n", err, memberTableStmt)
		return err
	}

	messageTableStmt := `
	CREATE TABLE IF NOT EXISTS "messages" (
    messageId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
    content TEXT,
    time INTEGER NOT NULL,
    memberid INTEGER NOT NULL,
    FOREIGN KEY(memberid) REFERENCES members (memberId)
  );
	`

	_, err = db.Exec(messageTableStmt)
	if err != nil {
		log.Printf("%q: %s\n", err, messageTableStmt)
		return err
	}

	reactionTableStmt := `
	CREATE TABLE "reactions" (
    reaction TEXT, 
    messageId INTEGER NOT NULL,
    memberId INTEGER NOT NULL,
    FOREIGN KEY(messageid) REFERENCES messages (messageId), 
    FOREIGN KEY(memberid) REFERENCES members (memberId),
    PRIMARY KEY(messageId, memberId) UNIQUE
  );
	`

	_, err = db.Exec(reactionTableStmt)
	if err != nil {
		log.Printf("%q: %s\n", err, reactionTableStmt)
		return err
	}

	photoTableStmt := `
	CREATE TABLE "photos" (
    uri text NOT NULL PRIMARY KEY UNIQUE, 
    timestamp integer NOT NULL,
    messageId INTEGER NOT NULL,
    FOREIGN KEY(messageid) REFERENCES messages (messageId)
  );
	`

	_, err = db.Exec(photoTableStmt)
	if err != nil {
		log.Printf("%q: %s\n", err, reactionTableStmt)
		return err
	}

	return nil
}

func CreateConnection(dbName string) (*DB, error) {
	db, err := sql.Open("sqlite3", fmt.Sprintf(dbName))
	if err != nil {
		return nil, err
	}

	database := &DB{
		DB: db,
	}

	return database, nil
}

func (db *DB) InsertParticipant(participantName string) error {
	stmt := `
  INSERT INTO 
    members(name)
    values(?)
    ON CONFLICT(name) DO NOTHING;
  `
	_, err := db.Exec(stmt, participantName)
	if err != nil {
		return err
	}

	return nil
}

func (db *DB) InsertMessage(message *types.Message) (messageId int64, err error) {
	stmt := `
  INSERT INTO 
    messages(content, time, [memberid])
    SELECT ?, ?, [memberid]
    FROM members
    WHERE name=?
    ON CONFLICT(messageId) DO NOTHING;
  `

	result, err := db.Exec(stmt, string(message.Content), time.Time(message.Timestamp).Unix(), string(message.SenderName))
	if err != nil {
		return 0, err
	}

	return result.LastInsertId()
}

func (db *DB) InsertReaction(reaction *types.Reaction, messageId int64) error {
	stmt := `
  INSERT INTO 
    reactions(reaction, [messageId], [memberid])
    SELECT ?, ?, [memberid]
    FROM members
    WHERE name=?
    ON CONFLICT([messageId]) DO NOTHING;
  `

	_, err := db.Exec(stmt, string(reaction.Reaction), messageId, string(reaction.Actor))
	if err != nil {
		return err
	}

	return nil
}
