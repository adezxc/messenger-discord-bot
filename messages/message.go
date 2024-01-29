package messages

import (
	"encoding/json"
	"fmt"
	"os"

	"github.com/adezxc/messenger-discord-bot/db"
	"github.com/adezxc/messenger-discord-bot/types"
)

const DbDefaultLocation = "./data/data.db"

func ReadMessageFile(filename string) (*types.MessageFile, error) {
	file, err := os.Open(filename)
	if err != nil {
		fmt.Fprintf(os.Stderr, "error: %v\n", err)
		os.Exit(1)
	}

	messageFile := &types.MessageFile{}

	decoder := json.NewDecoder(file)
	err = decoder.Decode(&messageFile)
	if err != nil {
		return nil, err
	}

	return messageFile, nil
}

func ProcessMessageFile(filename string) error {
	if filename == "" {
		return fmt.Errorf("No file provided")
	}

	messageFile, err := ReadMessageFile(filename)
	if err != nil {
		return err
	}
	fmt.Print(messageFile.Participants)

	db, err := db.CreateConnection(DbDefaultLocation)
	if err != nil {
		return err
	}
	defer db.Close()

	for _, participant := range messageFile.Participants {
		err = db.InsertParticipant(string(participant.Name))
		if err != nil {
			return err
		}
	}

	for _, message := range messageFile.Messages {
		id, err := db.InsertMessage(&message)
		if err != nil {
			return err
		}

		for _, reaction := range message.Reactions {
			err = db.InsertReaction(&reaction, id)
			if err != nil {
				return err
			}
		}
	}

	return nil
}
