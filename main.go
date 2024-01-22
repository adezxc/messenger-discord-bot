package main

import (
	"encoding/json"
	"fmt"
	"os"

	"./db"
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

	db := db.DbConnect("test.db")
}
