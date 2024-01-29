package db

import (
	"database/sql"
	"fmt"
	"log"

	_ "github.com/mattn/go-sqlite3"
)

func CreateDb(dbName string) error {
	db, err := sql.Open("sqlite3", fmt.Sprintf("./data/"+dbName))
	if err != nil {
		return err
	}
	defer db.Close()

	memberTableStmt := `
	CREATE TABLE "members" (
    memberId INTEGER NOT NULL PRIMARY KEY, 
    name TEXT
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
    reactionId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
    reaction TEXT, 
    messageId INTEGER NOT NULL,
    memberId INTEGER NOT NULL,
    FOREIGN KEY(messageid) REFERENCES messages (messageId), 
    FOREIGN KEY(memberid) REFERENCES members (memberId)
  );
	`

	_, err = db.Exec(reactionTableStmt)
	if err != nil {
		log.Printf("%q: %s\n", err, reactionTableStmt)
		return err
	}

	return nil
}
