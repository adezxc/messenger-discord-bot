package main

import (
	"fmt"
	"log"
	"os"

	"github.com/adezxc/messenger-discord-bot/db"
	"github.com/adezxc/messenger-discord-bot/messages"
	"github.com/urfave/cli"
)

func main() {
	app := &cli.App{
		Name:  "Messenger Discord Bot",
		Usage: "",
		Action: func(*cli.Context) error {
			fmt.Println("No serve function defined yet")
			os.Exit(0)
			return nil
		},
		Commands: []cli.Command{
			{
				Name:     "create-db",
				Aliases:  []string{},
				Usage:    "Create default DB with defined schema",
				Category: "",
				Action: func(cCtx *cli.Context) error {
					log.Printf("initiating database creation")
					err := db.CreateDb("./data/data.db")
					if err != nil {
						return err
					}

					return nil
				}},
			{
				Name:    "import-message-file",
				Aliases: []string{},
				Usage:   "Read Messenger's JSON file into the database",
				Action: func(cCtx *cli.Context) error {
					log.Printf("initiating message import")
					filename := cCtx.Args().First()

					err := messages.ProcessMessageFile(filename)
					if err != nil {
						return err
					}

					return nil
				},
			},
		},
	}
	if err := app.Run(os.Args); err != nil {
		log.Fatal(err)
	}

}
