package messages

import (
	"encoding/json"
	"fmt"
	"os"

	"github.com/adezxc/types"
)

func ReadMessages(filename string) error {
	file, err := os.Open(filename)
	if err != nil {
		fmt.Fprintf(os.Stderr, "error: %v\n", err)
		os.Exit(1)
	}

	messages := &types.MessageFile{}

	decoder := json.NewDecoder(file)
	err = decoder.Decode(&messages)
	if err != nil {
		fmt.Fprintf(os.Stderr, "error: %v\n", err)
		os.Exit(1)
	}

	outputBytes, err := json.Marshal(messages.Messages)
	if err != nil {
		fmt.Fprintf(os.Stderr, "error: %v\n", err)
		os.Exit(1)
	}

	fmt.Println(outputBytes)

	return nil
}
