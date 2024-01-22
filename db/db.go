package db

import (
	"database/sql"

	_ "github.com/mattn/go-sqlite3"
)

type Database struct {
	DB *sql.DB
}

func DbConnect(filename string) *Database {
	db, err := sql.Open("sqlite3", filename)
	if err != nil {
		return nil
	}

	return &Database{
		DB: db,
	}
}
