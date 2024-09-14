import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MyBot extends ListenerAdapter {

    private Map<String, String> customCommands = new HashMap<>();

    public MyBot() {
        loadCustomCommands("commands.txt");
    }

    private void loadCustomCommands(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+", 2);
                if (parts.length == 2) {
                    String command = parts[0];
                    String response = parts[1];
                    customCommands.put(command, response);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws LoginException {
        JDABuilder.createDefault("MTI4Mjk3NDQ3NDgzNjM3NzY2MA.G7pWfZ.xNgCTqt5TyJnnsXr6lkEng8Gzs-9SGZnb2gp-Q")
                .addEventListeners(new MyBot())
                .setStatus(OnlineStatus.ONLINE)
                .build();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String[] parts = message.split("\\s+", 2);
        String command = parts[0];

        if (customCommands.containsKey(command)) {
            String response = customCommands.get(command);
            MessageChannel channel = event.getChannel();
            channel.sendMessage(response).queue();
        } else if (command.equalsIgnoreCase("!hello")) {
            MessageChannel channel = event.getChannel();
            User user = event.getAuthor();
            channel.sendMessage("Hello, " + user.getAsMention() + "!").queue();
        } else if (command.equalsIgnoreCase("!ping")) {
            long startTime = System.currentTimeMillis();
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Pong!").queue(response -> {
                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;
                response.editMessageFormat("Pong! Response time: %d ms", responseTime).queue();
            });
        } else if (command.equalsIgnoreCase("!weather")) {
            if (parts.length == 2) {
                String location = parts[1];
                try {
                    String url = "http://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(location, "UTF-8") + "&units=metric&appid=your-openweathermap-api-key-here";
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");

                    int responseCode = con.getResponseCode();
                    if (responseCode == 200) {
                        Scanner scanner = new Scanner(con.getInputStream());
                        String response = scanner.useDelimiter("\\A").next();
                        scanner.close();
                        JSONObject json = new JSONObject(response);
                        double temp = json.getJSONObject
