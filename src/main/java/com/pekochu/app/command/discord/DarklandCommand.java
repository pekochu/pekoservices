package com.pekochu.app.command.discord;

public class DarklandCommand {

    private final static String MC_PEKOCHU = "227971372712853504";
    private final static String MC_RYUU = "426295423678808077";
    private final static String MC_IVAN = "523372457466527754";
    private final static String MC_PREFIX = "!chloe darkland ";

    // TODO: API Changed. Recreate all code from ground
    // From JDA to Javacord

//
//    public DarklandCommand(){ }
//
//    public void onCommandReceived(@NotNull GuildMessageReceivedEvent event, Long channelId) throws MalformedURLException {
//        String[] args = event.getMessage().getContentRaw().split("\\s+");
//
//        String command = args[2];
//        MinecraftInterface multicraftAPI = new MinecraftInterface();
//        MessageBuilder answer = new MessageBuilder();
//        EmbedBuilder info = new EmbedBuilder();
//
//        Long userId = event.getAuthor().getIdLong();
//        String stringUserId = String.valueOf(userId);
//        String userMention = new StringFormattedMessage("¡Hey, <@%s>!", stringUserId).toString();
//
//        event.getChannel().sendTyping().queue();
//        if(!(String.valueOf(channelId).equals("660256826713178113"))){
//            answer.append(userMention);
//            answer.append(", el canal por donde estas enviando tus solicitudes **no está autorizado**.");
//
//            answer.sendTo(event.getChannel()).queue();
//            return;
//        }
//
//        boolean isAuthorized = stringUserId.equals(MC_RYUU) || stringUserId.equals(MC_PEKOCHU)
//                || stringUserId.equals(MC_IVAN);
//
//        if(command.equalsIgnoreCase("reiniciar")){
//            answer.append(userMention);
//
//            if(isAuthorized){
//                JSONObject method = multicraftAPI.restartServer();
//                if(method.getBoolean("success")){
//                    answer.append(", reiniciando el servidor *Darkland*.");
//                }else{
//                    answer.append(", aparentemente hubó un error al atender tu solicitud. Intenta más tarde.");
//                    info = info.clear();
//                    info.setTitle("Detalles del error")
//                            .setDescription(Utils.discordSetMulticraftErrors(method.getJSONArray("errors")).toString())
//                            .setFooter(BotDiscord.FOOTER)
//                            .setColor(Color.RED);
//
//                    answer.setEmbed(info.build());
//                    info.clear();
//                }
//            }else{
//                answer.append(", no estas **autorizado** a enviar este comando.");
//            }
//        } else if(command.equalsIgnoreCase("detener")){
//            answer.append(userMention);
//
//            if(isAuthorized){
//                JSONObject method = multicraftAPI.stopServer();
//                if(method.getBoolean("success")){
//                    answer.append(", deteniendo el servidor *Darkland*.");
//                }else{
//                    answer.append(", aparentemente hubó un error al atender tu solicitud. Intenta más tarde.");
//                    info = info.clear();
//                    info.setTitle("Detalles del error")
//                            .setDescription(Utils.discordSetMulticraftErrors(method.getJSONArray("errors")).toString())
//                            .setFooter(BotDiscord.FOOTER)
//                            .setColor(Color.RED);
//
//                    answer.setEmbed(info.build());
//                    info.clear();
//                }
//            }else{
//                answer.append(", no estas **autorizado** a enviar este comando.");
//            }
//        } else if(command.equalsIgnoreCase("matar")){
//            answer.append(userMention);
//
//            if(isAuthorized){
//                JSONObject method = multicraftAPI.killServer();
//                if(method.getBoolean("success")){
//                    answer.append(", el servidor *Darkland* ha sido detenido forzosamente.");
//                }else{
//                    answer.append(", aparentemente hubó un error al atender tu solicitud. Intenta más tarde.");
//                    info = info.clear();
//                    info.setTitle("Detalles del error")
//                            .setDescription(Utils.discordSetMulticraftErrors(method.getJSONArray("errors")).toString())
//                            .setFooter(BotDiscord.FOOTER)
//                            .setColor(Color.RED);
//
//                    answer.setEmbed(info.build());
//                    info.clear();
//                }
//            }else{
//                answer.append(", no estas **autorizado** a enviar este comando.");
//            }
//        }
//        // Sin necesidad de autorización
//        else if (command.equalsIgnoreCase("iniciar")){
//            JSONObject method = multicraftAPI.startServer();
//            if(method.getBoolean("success")){
//                answer.append(userMention);
//                answer.append(", iniciando el servidor *Darkland*.");
//            }else{
//                answer.append(userMention);
//                answer.append(", aparentemente hubó un error al atender tu solicitud. Intenta más tarde.");
//                info = info.clear();
//                info.setTitle("Detalles del error")
//                        .setDescription(Utils.discordSetMulticraftErrors(method.getJSONArray("errors")).toString())
//                        .setFooter(BotDiscord.FOOTER)
//                        .setColor(Color.RED);
//
//                answer.setEmbed(info.build());
//                info.clear();
//            }
//        } else if(command.equalsIgnoreCase("status")){
//            // Utils.discordMentionUser(answer, authorMention);
//
//            JSONObject method = multicraftAPI.getStatus();
//            if(method.getBoolean("success")){
//                JSONObject data = method.getJSONObject("data");
//                answer.append(", el servidor se encuentra: ");
//                answer.append(data.getString("status"), MessageBuilder.Formatting.BOLD, MessageBuilder.Formatting.ITALICS);
//                answer.append(".");
//            }else{
//                answer.append(userMention);
//                answer.append(", aparentemente hubó un error al atender tu solicitud. Intenta más tarde.");
//                info = info.clear();
//                info.setTitle("Detalles del error")
//                        .setDescription(Utils.discordSetMulticraftErrors(method.getJSONArray("errors")).toString())
//                        .setFooter(BotDiscord.FOOTER)
//                        .setColor(Color.RED);
//
//                answer.setEmbed(info.build());
//                info.clear();
//            }
//        } else if(command.equalsIgnoreCase("recursos")){
//            // Utils.discordMentionUser(answer, authorMention);
//
//            JSONObject method = multicraftAPI.getServerResources();
//            if(method.getBoolean("success")){
//                JSONObject data = method.getJSONObject("data");
//                answer.append(userMention);
//                answer.append(", los recursos del servidor de Minecraft son los siguientes actualmente:");
//
//                info = info.clear();
//                info.setTitle("Detalles del servidor")
//                        .setDescription("CPU al ")
//                        .appendDescription(data.getString("cpu").concat("%"))
//                        .appendDescription("\nMemoria RAM al ")
//                        .appendDescription(data.getString("memory").substring(0, 5).concat("%"))
//                        .setFooter(BotDiscord.FOOTER)
//                        .setColor(Color.GREEN);
//            }else{
//                answer.append(userMention);
//                answer.append(", aparentemente hubó un error al atender tu solicitud. Intenta más tarde.");
//                info = info.clear();
//                info.setTitle("Detalles del error")
//                        .setDescription(Utils.discordSetMulticraftErrors(method.getJSONArray("errors")).toString())
//                        .setFooter(BotDiscord.FOOTER)
//                        .setColor(Color.RED);
//            }
//            answer.setEmbed(info.build());
//            info.clear();
//        } else if(command.equalsIgnoreCase("ayuda")){
//            answer.append(", consulta  para los detalles e instrucciones");
//            answer.append(" de cómo jugar con nosotros en el link adjunto a este mensaje. \n");
//            answer.append(new URL("https://pekochu.com/minecraft"));
//        } else if(command.equalsIgnoreCase("comandos")){
//            answer.append(", mis comandos para *Darkland* son los siguientes:\n\n**");
//
//            answer.append(MC_PREFIX.concat("iniciar**: para iniciar el servidor. \n**"));
//            answer.append(MC_PREFIX.concat("status**: consultar el estado del servidor. \n**"));
//            answer.append(MC_PREFIX.concat("recursos**: consulta el consumo de *Darkland*.\n**"));
//            answer.append(MC_PREFIX.concat("comando**: consulta los comandos disponibles para *Darkland*.\n**"));
//            answer.append(MC_PREFIX.concat("ayuda**: ¿cómo conectarte al servidor *Darkland*?\n\n"));
//
//            answer.append("Los siguientes comandos sólo pueden ser ejecutados por los admins. de *Darkland*:\n**");
//            answer.append(MC_PREFIX.concat("reiniciar**: reiniciar el servidor.\n**"));
//            answer.append(MC_PREFIX.concat("detener**: detener el servidor.\n**"));
//            answer.append(MC_PREFIX.concat("matar**: forzar al servidor a detenerse."));
//        }else{
//            answer.append(", lo siento, no conozco este comando. Intenta con **");
//            answer.append(MC_PREFIX.concat("comando** para conocer mis comandos."));
//        }
//
//        answer.sendTo(event.getChannel()).queue();
//    }

}
