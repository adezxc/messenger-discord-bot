package main

import (
	"encoding/json"
	"fmt"
	"os"

	"github.com/meilisearch/meilisearch-go"
)

func main() {
	if len(os.Args) != 2 {
		fmt.Fprintf(os.Stderr, "File name not provided\n")
		os.Exit(1)
	}
	filename := os.Args[1]

	file, err := os.Open(filename)
	if err != nil {
		fmt.Fprintf(os.Stderr, "error: %v\n", err)
		os.Exit(1)
	}

	messages := &MessageFile{}

	decoder := json.NewDecoder(file)
	err = decoder.Decode(&messages)
	if err != nil {
		fmt.Fprintf(os.Stderr, "error: %v\n", err)
		os.Exit(1)
	}
	client := meilisearch.NewClient(meilisearch.ClientConfig{
		Host:   "http://localhost:7700",
		APIKey: "aSampleMasterKey",
	})
	fmt.Println(messages.Messages[0].Content)

	outputBytes, err := json.Marshal(messages.Messages)
	if err != nil {
		fmt.Fprintf(os.Stderr, "error: %v\n", err)
		os.Exit(1)
	}

	_, err = client.Index("Messages").AddDocuments(outputBytes, "timestamp_ms")
	if err != nil {
		panic(err)
	}

	index, err := client.GetIndex("Messages")
	if err != nil {
		panic(err)
	}
	resp, err := index.GetDisplayedAttributes()
	if err != nil {
		panic(err)
	}
	fmt.Println(&resp)

}
