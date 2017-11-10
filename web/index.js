(() => {

    /*
        JSON for chat:

        {
            username: value,
            message: value
        }

        JSON for hash:
        {
            hashString: value,
            value: value
        }
    */

    const chatUrl = "http://104.197.129.249/chatroom/msgtest.json";
    const hashUrl = "http://104.197.129.249/HaaS/";

    function initTestChat() {

        const $chatbox = $("#chat-text-area");

        for (let i = 0; i < 20; i++) {
            const $comment = $('<div>', {});
            $comment.text("This is a test comment");
            $chatbox.append($comment);
            $chatbox.append('<hr />');
        }

        const $comment = $('<div>', {});
        $comment.text("This is a test comment");
        $chatbox.append($comment);
    }

    function initTestHashTable() {
        const $hashTable = $("#hash-table");

        for (let i = 0; i < 10; i++) {
            const $hashRow = $('<tr>', {});
            const $hashString = $('<td>', {});
            const $hashValue = $('<td>', {});
            
            const $increment = $('<td>', {});
            const $incrementButton = $('<button>', {});
            $incrementButton.text("Increment");
            $increment.append($incrementButton);

            $incrementButton.on("click", ((e) => {
                e.preventDefault();
                $hashValue.html(parseInt($hashValue.html()) + 1);
            }));

            const $delete = $('<td>', {});
            const $deleteButton = $('<button>', {});
            $deleteButton.text("Delete");
            $delete.append($deleteButton);

            $hashString.html(i);
            $hashValue.html(0);

            $hashRow.append($hashString);
            $hashRow.append($hashValue);

            $hashRow.append($increment);
            $hashRow.append($delete);

            $hashTable.append($hashRow);
        }
    }

    function loadChat() {
        $.ajax({
            url: chatUrl,
            type: "GET",
            dataType: "JSON",
            success: (comments) => {
                if (comments) {
                    $("#chat-text-area").empty();
                    comments.messages.forEach((comment) => {
                        displayComment(comment);
                    });
                } else {
                    console.log("Could not load comments");
                }
            },
            error: (request, status, error) => {
                console.log(error, status, request);
            }
        });
    }

    function loadHashTable() {
        $.ajax({
            url: hashUrl,
            type: "GET",
            dataType: "JSON",
            success: (hashes) => {
                if (hashes) {
                    hashes.records.forEach((hash) => {
                        displayHash(hash);
                    });
                }
            },
            error: (request, status, error) => {
                console.log(error, status, request);
            }
        });
    }

    function displayComment(comment) {
        const $chatbox = $("#chat-text-area");
        const $commentDiv = $('<div>', {});
        $commentDiv.text(comment.username + ": " + comment.message);
        $chatbox.append($commentDiv);
        $chatbox.append('<hr />');
    }

    function displayHash(hash) {
        const $hashTable = $("#hash-table");

        const $hashRow = $('<tr>', {});
        const $hashString = $('<td>', {});
        const $hashValue = $('<td>', {});
        
        const $increment = $('<td>', {});
        const $incrementButton = $('<button>', {});
        $incrementButton.text("Increment");
        $increment.append($incrementButton);

        $incrementButton.on("click", ((e) => {
            e.preventDefault();
            const val = parseInt($hashValue.html()) + 1;

            const dataHash = {
                hashString: $hashString.html(),
                value: val
            };
            
            $.ajax({
                url: hashUrl,
                type: "PUT",
                data: JSON.stringify(dataHash),
                dataType: "JSON",
                success: (hashes) => {
                    $hashTable.empty();
                    hashes.records.forEach((hash) => {
                        displayHash(hash);
                    });
                    loadChat();
                },
                error: (request, status, error) => {
                    console.log(error, status, request);
                }
            });
        }));

        const $delete = $('<td>', {});
        const $deleteButton = $('<button>', {});
        $deleteButton.text("Delete");
        $delete.append($deleteButton);

        $deleteButton.on("click", ((e) => {
            e.preventDefault();

            const dataHash = {
                hashString: $hashString.html(),
                value: parseInt($hashValue.html())
            };

            $.ajax({
                url: hashUrl,
                type: "DELETE",
                data: JSON.stringify(dataHash),
                dataType: "JSON",
                success: (hashes) => {
                    if (hashes) {
                        $hashTable.empty();
                        hashes.records.forEach((hash) => {
                            displayHash(hash);
                        });
                        loadChat();
                    }
                    
                },
                error: (request, status, error) => {
                    console.log(error, status, request);
                }
            });
            
        }));

        $hashString.html(hash.hashString);
        $hashValue.html(hash.value);

        $hashRow.append($hashString);
        $hashRow.append($hashValue);

        $hashRow.append($increment);
        $hashRow.append($delete);

        $hashTable.append($hashRow);
    }

    $("#send-message").on("click", ((e) => {
        e.preventDefault();
        console.log("HERECHAT");
        const name = $("#username-input").val();
        const message = $("#message-input").val();

        const comment = {
            username: name,
            message: message
        };

        if (name != "" && message != "") {
            $.ajax({
                url: chatUrl,
                type: "POST",
                data: JSON.stringify(comment),
                dataType: "JSON",
                success: (comments) => {
                    if (comments) {
                        $("#chat-text-area").empty();
                        comments.messages.forEach((comment) => {
                            displayComment(comment);
                        });
                        
                    } else {
                        console.log("Message not sent");
                    }
                },
                error: (request, status, error) => {
                    console.log(error, status, request);
                }
            });
        }
    }));

    $("#send-hash").on("click", ((e) => {
        console.log("HEREHASH");
        e.preventDefault();

        const $hashTable = $("#hash-table");
        const name = $("#hash-input").val();

        const hash = {
            hashString: name,
            value: 0
        };

        if (name != "") {
            $.ajax({
                url: hashUrl,
                type: "POST",
                data: JSON.stringify(hash),
                dataType: "JSON",
                success: (hashes) => {
                    if (hashes) {
                        $hashTable.empty();
                        hashes.records.forEach((hash) => {
                            displayHash(hash);
                        });
                        loadChat();
                    }
                },
                error: (request, status, error) => {
                    console.log(error, status, request);
                }
            });
        }
    }));


    $(document).ready(() => {
        // initTestChat();
        // Uncomment below line and comment out above when ready to deploy
        loadChat();

        // initTestHashTable();
        // Same thing goes for here.
        loadHashTable();
    });
})();