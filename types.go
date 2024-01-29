package main

import (
	"encoding/json"
	"strconv"
	"strings"
	"time"

	"golang.org/x/text/encoding/charmap"
)

type UnixTime time.Time

func (j *UnixTime) UnmarshalJSON(b []byte) error {
	s := strings.Trim(string(b), "\"")
	unixTime, err := strconv.ParseInt(s, 10, 64)
	if err != nil {
		return err
	}

	t := time.Unix(unixTime/1000, 1000000*(unixTime%1000))
	*j = UnixTime(t)
	return nil
}

func (j UnixTime) MarshalJSON() ([]byte, error) {
	return json.Marshal(time.Time(j).Unix())
}

type MessengerString string

func (m *MessengerString) UnmarshalJSON(b []byte) error {
	// We need to first decode the JSON, then Encode it using LATIN-1 due to Facebook's mojibake
	// https://stackoverflow.com/questions/50008296/facebook-json-badly-encoded
	var decodedBytes string
	err := json.Unmarshal(b, &decodedBytes)
	if err != nil {
		panic(err)
	}

	encoder := charmap.ISO8859_1.NewEncoder()
	encodedBytes, err := encoder.Bytes([]byte(decodedBytes))
	if err != nil {
		panic(err)
	}

	*m = MessengerString(encodedBytes)
	return nil
}

func (m MessengerString) MarshalJSON() ([]byte, error) {
	return json.Marshal(string(m))
}

type MessageFile struct {
	Participants []Participant   `json:"participants"`
	Messages     []Message       `json:"messages"`
	Title        MessengerString `json:"title"`
	ThreadPath   MessengerString `json:"thread_path"`
}

type Message struct {
	SenderName MessengerString `json:"sender_name"`
	Timestamp  UnixTime        `json:"timestamp_ms"`
	Content    MessengerString `json:"content"`
	Reactions  []Reaction      `json:"reactions,omitempty"`
}

type Participant struct {
	Name MessengerString `json:"name"`
}

type Reaction struct {
	Reaction MessengerString `json:"reaction"`
	Actor    MessengerString `json:"actor"`
}

type ZincMessage struct {
	SenderName MessengerString `json:"sender_name"`
	Timestamp  UnixTime        `json:"timestamp_ms"`
	Content    MessengerString `json:"content"`
	Reactions  []ZincReaction  `json:"reactions,omitempty"`
}

type ZincMessages struct {
	Index    string    `json:"index"`
	Messages []Message `json:"records"`
}

type ZincReaction struct {
	Index     string     `json:"index"`
	Reactions []Reaction `json:"reactions"`
}
