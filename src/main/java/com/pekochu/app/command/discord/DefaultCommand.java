package com.pekochu.app.command.discord;

public class DefaultCommand {

    // TODO: API Changed. Recreate all code from ground
    // From JDA to Javacord

//    public void onCommandReceived(@NotNull GuildMessageReceivedEvent event){
//        String[] args = event.getMessage().getContentRaw().split("\\s+");
//        event.getChannel().sendTyping().queue();
//
//        String command = args[1];
//        MessageBuilder answer = new MessageBuilder();
//        EmbedBuilder info = new EmbedBuilder();
//
//        Long userId = event.getAuthor().getIdLong();
//        String stringUserId = String.valueOf(userId);
//        String userMention = new StringFormattedMessage("¡Hey, <@%s>!", stringUserId).toString();
//        if(command.equalsIgnoreCase("help")){
//            answer.append(userMention);
//            answer.append(", para español, intenta con ");
//            answer.append(BotDiscord.PREFIX.concat(" ayuda"));
//            // Help Content
//            info = info.clear();
//            info.setDescription("")
//                    .setTitle("Help content")
//                    .setColor(Color.GREEN)
//                    .setFooter(BotDiscord.FOOTER);
//            answer.setEmbed(info.build());
//            info.clear();
//        }else if(command.equalsIgnoreCase("ayuda")){
//            answer.append(userMention);
//            answer.append(", for english, please try ");
//            answer.append(BotDiscord.PREFIX.concat(" help"));
//            // Contenido de la ayuda
//            info = info.clear();
//            info.setDescription("")
//                    .setTitle("Sección de ayuda")
//                    .setColor(Color.GREEN)
//                    .setFooter(BotDiscord.FOOTER);
//            answer.setEmbed(info.build());
//            info.clear();
//        }else{
//            answer.append(userMention);
//            answer.append(", can't undestand you. I'm sorry\nSpanish: No pude entenderte, lo siento.");
//            // Contenido de la ayuda
//            info = info.clear();
//            info.setDescription(command)
//                    .setColor(Color.RED)
//                    .setTitle("Detalles del error")
//                    .appendDescription(" no es un comando valido")
//                    .setFooter(BotDiscord.FOOTER);
//
//            answer.setEmbed(info.build());
//            info.clear();
//        }
//
//        answer.sendTo(event.getChannel()).queue();
//    }
}
