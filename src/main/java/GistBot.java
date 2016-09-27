import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistBuilder;
import org.kohsuke.github.GitHub;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Scanner;

public class GistBot extends TelegramLongPollingBot {

    private String botToken;

    public GistBot(String token) {
        this();
        this.botToken = token;
    }

    public GistBot() {

    }

    @Override
    public String getBotToken() {
        if (this.botToken == null) {
            System.out.println("Please provide a bot token:");
            this.botToken = new Scanner(System.in).nextLine();
        }
        return this.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            handleMessage(update);
        } catch (Exception e) {
        }
    }

    @Override
    public String getBotUsername() {
        return "GistGreaterAllBot";
    }

    private void handleMessage(Update update) throws InvalidObjectException {

        Message message = update.getMessage();

        if (message.getText().equalsIgnoreCase("/start") && !(message.isSuperGroupMessage() || message.isGroupMessage())) {
            sendMessage("Send a message to gistify it!", message);
        } else {
            String input = message.getText();
            if (message.isGroupMessage() || message.isSuperGroupMessage()) {
                if (input.startsWith("@GistGreaterAllBot ")) {
                    input = input.substring(19);
                } else return;
            }
            try {
                //relying on ~/.github
                GitHub root = GitHub.connect();
                GHGistBuilder ghGistBuilder = new GHGistBuilder(root);
                ghGistBuilder.public_(false);
                ghGistBuilder.file(message.getFrom().getFirstName() + "'s gist via GistGreaterAllBot", input);

                GHGist ghGist = ghGistBuilder.create();
                sendMessage(ghGist.getHtmlUrl().toString(), message);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message, Message target) {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setText(message);
        sendMessageRequest.setChatId(target.getChatId().toString());
        try {
            sendMessage(sendMessageRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public static void main(String... args) throws IOException {

        GistBot gistBot;
        if (args.length > 0)
            gistBot = new GistBot(args[0]);
        else
            gistBot = new GistBot();
        GistBot finalGistBot = gistBot;
        new Thread(() -> {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                telegramBotsApi.registerBot(finalGistBot);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
