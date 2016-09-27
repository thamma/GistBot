import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistBuilder;
import org.kohsuke.github.GitHub;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.math.BigInteger;
import java.util.*;

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
        return "GistBot";
    }

    private void handleMessage(Update update) throws InvalidObjectException {

        Message message = update.getMessage();

        if (message.getText().equalsIgnoreCase("/start")) {
            sendMessage("Welcome!\nSend an integer to factorize it!", message);
        } else {
            String input = message.getText();
            try {

                GitHub root = GitHub.connectAnonymously();
                GHGistBuilder ghGistBuilder = new GHGistBuilder(root);
                ghGistBuilder.file("testfile", "content\nmorecontent");

                GHGist ghGist = ghGistBuilder.create();
                System.out.println(ghGist.getHtmlUrl());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //private Map<BigInteger, BigInteger> powerify(List<BigInteger> list) {
    //    Map<BigInteger, BigInteger> out = new HashMap<>();
//
    //    for (BigInteger l : list) {
    //        if (out.containsKey(l)) {
    //            out.put(l, out.get(l).add(BigInteger.ONE));
    //        } else {
    //            out.put(l, BigInteger.ONE);
    //        }
    //    }
    //    return out;
    //}

   private Map<Long, Long> powerify(List<Long> list) {
       Map<Long, Long> out = new HashMap<>();
       for (long l : list) {
           if (out.containsKey(l)) {
               out.put(l, out.get(l) + 1);
           } else {
               out.put(l, 1L);
           }
       }
       return out;
   }

    private List<BigInteger> factor(BigInteger l) {
        List<BigInteger> out = new ArrayList<>();

        if (l.compareTo(BigInteger.ZERO) == -1) {
            out.add(BigInteger.valueOf(-1));
            l = l.multiply(BigInteger.valueOf(-1));
        }
        if (l.compareTo(BigInteger.ONE) == 0) {
            out.add(BigInteger.ONE);
            return out;
        }
        findDivisors:
        while (l.compareTo(BigInteger.ONE) != 0) { //while (l != 1)
            for (int i = 2; BigInteger.valueOf(i).compareTo(sqrt(l).add(BigInteger.valueOf(2))) == -1; i++) {
                if (l.mod(BigInteger.valueOf(i)).compareTo(BigInteger.ZERO) == 0) {
                    out.add(BigInteger.valueOf(i));
                    System.out.println(i);
                    l = l.divide(BigInteger.valueOf(i));
                    continue findDivisors;
                }
            }
            out.add(l);
            l = BigInteger.ONE;
        }
        System.out.println("sorting");
        out.sort(BigInteger::compareTo);
        System.out.println("sorted");
        return out;
    }

    public static BigInteger sqrt(BigInteger x) {
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength() / 2);
        BigInteger div2 = div;
        // Loop until we hit the same value twice in a row, or wind
        // up alternating.
        for (; ; ) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2))
                return y;
            div2 = div;
            div = y;
        }
    }

    private List<Long> factor(long l) {
        List<Long> out = new ArrayList<>();

        if (l < 0) {
            out.add(-1L);
            l *= -1;
        }
        if (l == 1) {
            out.add(1L);
            return out;
        }
        findDivisors:
        while (l != 1) {
            //System.out.println(l);
            for (int i = 2; i < Math.sqrt(l) + 1; i++) {
                if (l % i == 0) {
                    out.add((long) i);
                    l /= i;
                    continue findDivisors;
                }
            }
            out.add(l);
            l /= l;
        }
        out.sort(Long::compare);
        return out;
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

        GitHub root = GitHub.connect();
        GHGistBuilder ghGistBuilder = new GHGistBuilder(root);
        ghGistBuilder.file("testfile", "content\nmorecontent");


        GHGist ghGist = ghGistBuilder.create();
        System.out.println(ghGist.getHtmlUrl());

       //GistBot factorizerBot;
       //if (args.length > 0)
       //    factorizerBot = new GistBot(args[0]);
       //else
       //    factorizerBot = new GistBot();
       //GistBot finalfactorizerBot = factorizerBot;
       //new Thread(() -> {
       //    TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
       //    try {
       //        telegramBotsApi.registerBot(finalfactorizerBot);
       //    } catch (TelegramApiException e) {
       //        e.printStackTrace();
       //    }
       //}).start();
    }
}
