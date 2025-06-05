package org.corpse.telling.death;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bson.Document;
import org.corpse.telling.death.utils.HttpPetition;

import com.mongodb.MongoSecurityException;
import com.mongodb.MongoSocketException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SpringLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Color;
import javax.swing.SwingConstants;

public class CorpseAutopsy {

	private JFrame frame;
	private Properties p = new Properties();
	private JTextField ipServerWrite;
	private JLabel ipResponse;
	private JTextField commandText;
	private JTextField ipMongoWrite;
	private static final String FILE = "config.properties";
    private static Properties dataProperties = new Properties();
    private static File data = new File(FILE);
    private static final List<String> LANGUAGES = List.of("en", "es", "fr", "de");
    private List<String> deathData;
    private int currentPage = 0;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (data.exists()) {
		            try (FileInputStream in = new FileInputStream(data)) {
		                dataProperties.load(in);
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        } else {
		        	dataProperties.setProperty("ip", "localhost:4567");
		        	dataProperties.setProperty("mongo", "127.0.0.1");
		        	dataProperties.setProperty("language", "en");
		        	save();
		        	try (FileInputStream in = new FileInputStream(data)) {
		                dataProperties.load(in);
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
				
				try {
					CorpseAutopsy window = new CorpseAutopsy();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private static void save() {
		try (FileOutputStream out = new FileOutputStream(FILE)) {
            dataProperties.store(out, "Configuración de la interfaz");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	/**
	 * Create the application.
	 */
	public CorpseAutopsy() {
	    try {
	        p.load(getClass().getClassLoader().getResourceAsStream(switch(dataProperties.getProperty("language")) {
	        case "es" -> "es.properties";
            case "fr" -> "fr.properties";
            case "de" -> "de.properties";
            case "pt" -> "pt.properties";
            default -> "en.properties";
	        } 
	        ));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    initialize();
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(120, 80, 44));
		frame.setBounds(100, 100, 615, 380);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("Servidor.png"));
	    frame.setIconImage(icon.getImage());
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setBackground(new Color(0, 128, 0));
		
		
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel lenguage = new JPanel();
		lenguage.setBackground(Color.GRAY);
		tabbedPane.addTab(p.getProperty("lenguage"), null, lenguage, null);
		lenguage.setLayout(null);
		
		JComboBox lenguages = new JComboBox();
		lenguages.setBackground(Color.LIGHT_GRAY);
		for (String lang : LANGUAGES) {
            lenguages.addItem(lang);
        }
		lenguages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    String selected = (String) lenguages.getSelectedItem();
			    dataProperties.setProperty("language", selected);
			    save();
			    p = new Properties();
			    try {
			        p.load(getClass().getClassLoader().getResourceAsStream(
			            switch(selected) {
			                case "es" -> "es.properties";
			                case "fr" -> "fr.properties";
			                case "de" -> "de.properties";
			                case "pt" -> "pt.properties";
			                default -> "en.properties";
			            }
			        ));
			    } catch (IOException ex) {
			        ex.printStackTrace();
			    }
			    frame.dispose();
			    initialize();
			    frame.setVisible(true);
			}
		});
		lenguages.setBounds(126, 78, 274, 33);
		lenguage.add(lenguages);
		
		JPanel registries = new JPanel();
		registries.setBackground(Color.GRAY);
		tabbedPane.addTab(p.getProperty("registries"), null, registries, null);
		registries.setLayout(null);
		
		JLabel page = new JLabel("xx/xx");
		page.setFont(new Font("Tahoma", Font.BOLD, 16));
		page.setBounds(241, 281, 45, 13);
		registries.add(page);
		
		JButton left = new JButton("<--");
		left.setBackground(Color.LIGHT_GRAY);
		left.setBounds(54, 273, 85, 28);
		registries.add(left);
		
		JButton right = new JButton("-->");
		right.setBackground(Color.LIGHT_GRAY);
		right.setBounds(384, 273, 85, 28);
		registries.add(right);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(54, 21, 415, 210);
		JTextArea textArea = new JTextArea();
		textArea.setForeground(Color.ORANGE);
		textArea.setBackground(Color.BLACK);
        scrollPane.setViewportView(textArea);
        registries.add(scrollPane);
        
        left.addActionListener(e -> {
		    if (currentPage > 0) {
		        currentPage--;
		        page.setText((currentPage + 1) + "/" + deathData.size());
		        textArea.setText(deathData.get(currentPage));
		    }
		});
        
        right.addActionListener(e -> {
            if (currentPage < deathData.size() - 1) {
                currentPage++;
                page.setText((currentPage + 1) + "/" + deathData.size());
                textArea.setText(deathData.get(currentPage));
            }
        });
		
		JPanel players = new JPanel(new FlowLayout());
		players.setBackground(Color.GRAY);
		tabbedPane.addTab(p.getProperty("players"), null, players, null);
		
		JPanel commands = new JPanel();
		commands.setBackground(Color.GRAY);
		tabbedPane.addTab(p.getProperty("commands"), null, commands, null);
		
		tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				switch(tabbedPane.getSelectedIndex()) {
				case 1:
				    deathData = fetchDeaths();
				    textArea.setText("");

				    if (!deathData.isEmpty()) {
				        currentPage = 0;
				        page.setText((currentPage + 1) + "/" + deathData.size());
				        textArea.setText(deathData.get(currentPage));
				    } else {
				        page.setText("0/0");
				    }
				    break;
				case 2:
					players.removeAll();
					List<String> playerNames = HttpPetition.getPlayers(dataProperties);
					playerNames.forEach(p -> players.add(playerButton(p)));
					players.revalidate();
	                players.repaint();
					break;
				}
			}
			});
		
		commandText = new JTextField();
		commandText.setForeground(Color.ORANGE);
		commandText.setBackground(Color.BLACK);
		commandText.setColumns(10);
		
		String[] com = String.valueOf(p.get("commandList")).split(",");
		JComboBox<String> commandOptions = new JComboBox<String>(com);
		commandOptions.setBackground(Color.LIGHT_GRAY);
		commandOptions.setForeground(Color.BLACK);
		commandOptions.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switch((Integer)commandOptions.getSelectedIndex()) {
				case 0:
					commandText.setText("execute as [player] at @s run summon minecraft:[entity] ~ ~ ~");
					break;
				case 1:
					commandText.setText("gamemode [mode] [player]");
					break;
				case 2:
					commandText.setText("execute as [player] at @s run summon minecraft:lightning_bolt ~ ~ ~");
					break;
				case 3:
					commandText.setText("effect [player] minecraft:[effect] [duration] [level]");
					break;
				case 4:
					commandText.setText("gamerule <gamerule> <value>");
					break;
				case 5:
					commandText.setText("spawnpoint [jugador] [x] [y] [z]");
					break;
				case 6:
					commandText.setText("tp [player] [new place]");
					break;
				case 7:
					commandText.setText("give [player] minecraft:[item] [number]");
					break;
				case 8:
					commandText.setText("execute as [jugador] at @s run summon ocelot ~ ~ ~ {Trusting:1,Passengers:[{id:zombie_villager,VillagerData:{type:swamp,profession:nitwit,level:5},IsBaby:1,CustomName:'[{\"text\":\"P\",\"bold\":true,\"color\":\"#ff0000\"},{\"text\":\"E P\",\"color\":\"#fff55]\"},{\"text\":\"E\",\"color\":\"#fff55]\"}]',Health:200,PersistenceRequired:1b,HandItems:[{id:end_rod,components:{custom_name:'[\"\",{\"text\":\"Oveja XD\",\"italic\":false}]',lore:['[\"\",{\"text\":\"No hace falta decir donde ha estado.\",\"italic\":false}]'],rarity:epic,enchantments:{levels:{bane_of_arthropods:20,binding_curse:1,blast_protection:10,looting:10,projectile_protection:10,punch:20,sharpness:20}}},count:1},{id:sticky_piston,count:1}],HandDropChances:[1f,1f],ArmorItems:[{},{},{},{id:milk_bucket,count:1}],ArmorDropChances:[0f,0f,0f,0f],attributes:[{id:\"generic.max_health\",base:200f}]}]}");
					break;
				default:
					break;
				}
			}
		});
		
		JButton execute = new JButton(p.getProperty("execute"));
		execute.setBackground(Color.LIGHT_GRAY);
		execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand(commandText.getText(),dataProperties);
				commandText.setText(null);
			}
		});
		GroupLayout gl_commands = new GroupLayout(commands);
		gl_commands.setHorizontalGroup(
			gl_commands.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_commands.createSequentialGroup()
					.addGap(71)
					.addGroup(gl_commands.createParallelGroup(Alignment.LEADING)
						.addComponent(commandText, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_commands.createSequentialGroup()
							.addGap(38)
							.addComponent(commandOptions, GroupLayout.PREFERRED_SIZE, 326, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_commands.createSequentialGroup()
							.addGap(159)
							.addComponent(execute)))
					.addContainerGap(109, Short.MAX_VALUE))
		);
		gl_commands.setVerticalGroup(
			gl_commands.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_commands.createSequentialGroup()
					.addGap(70)
					.addComponent(commandText, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(commandOptions, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addGap(60)
					.addComponent(execute)
					.addContainerGap(92, Short.MAX_VALUE))
		);
		commands.setLayout(gl_commands);
		
		JPanel world = new JPanel();
		tabbedPane.addTab(p.getProperty("world"), null, world, null);
		world.setLayout(new GridLayout(3, 0, 0, 0));
		
		JPanel difficulty = new JPanel();
		world.add(difficulty);
		difficulty.setLayout(new GridLayout(1, 0, 0, 0));
		
		JButton peaceful = new JButton(p.getProperty("peaceful"));
		peaceful.setBackground(Color.LIGHT_GRAY);
		peaceful.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand("peaceful",dataProperties);
			}
		});
		difficulty.add(peaceful);
		
		JButton easy = new JButton(p.getProperty("easy"));
		easy.setBackground(Color.LIGHT_GRAY);
		easy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand("easy",dataProperties);
			}
		});
		difficulty.add(easy);
		
		JButton normal = new JButton(p.getProperty("normal"));
		normal.setBackground(Color.LIGHT_GRAY);
		normal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand("normal",dataProperties);
			}
		});
		difficulty.add(normal);
		
		JButton hard = new JButton(p.getProperty("hard"));
		hard.setBackground(Color.LIGHT_GRAY);
		hard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand("hard",dataProperties);
			}
		});
		difficulty.add(hard);
		
		JPanel weather = new JPanel();
		world.add(weather);
		weather.setLayout(new GridLayout(1, 0, 0, 0));
		
		JButton clear = new JButton(p.getProperty("clear"));
		clear.setBackground(Color.LIGHT_GRAY);
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand("clear",dataProperties);
			}
		});
		weather.add(clear);
		
		JButton rain = new JButton(p.getProperty("rain"));
		rain.setBackground(Color.LIGHT_GRAY);
		rain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand("rain",dataProperties);
			}
		});
		weather.add(rain);
		
		JButton storm = new JButton(p.getProperty("storm"));
		storm.setBackground(Color.LIGHT_GRAY);
		storm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand("storm",dataProperties);
			}
		});
		weather.add(storm);
		
		JPanel time = new JPanel();
		world.add(time);
		time.setLayout(new GridLayout(1, 0, 0, 0));
		
		JButton day = new JButton(p.getProperty("day"));
		day.setBackground(Color.LIGHT_GRAY);
		day.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand("day",dataProperties);
			}
		});
		time.add(day);
		
		JButton noon = new JButton(p.getProperty("noon"));
		noon.setBackground(Color.LIGHT_GRAY);
		noon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand("noon",dataProperties);
			}
		});
		time.add(noon);
		
		JButton night = new JButton(p.getProperty("night"));
		night.setBackground(Color.LIGHT_GRAY);
		night.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand("night",dataProperties);
			}
		});
		time.add(night);
		
		JButton midnight = new JButton(p.getProperty("midnight"));
		midnight.setBackground(Color.LIGHT_GRAY);
		midnight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HttpPetition.sendCommand("midnight",dataProperties);
			}
		});
		time.add(midnight);
		
		JPanel ipAsign = new JPanel();
		ipAsign.setBackground(Color.GRAY);
		ipAsign.setForeground(Color.GRAY);
		tabbedPane.addTab(p.getProperty("ip"), null, ipAsign, null);
		
		ipServerWrite = new JTextField();
		ipServerWrite.setHorizontalAlignment(SwingConstants.CENTER);
		ipServerWrite.setForeground(Color.ORANGE);
		ipServerWrite.setBackground(Color.BLACK);
		ipServerWrite.setColumns(10);
		
		JButton ipServer = new JButton(p.getProperty("conectIpServer"));
		ipServer.setBackground(Color.LIGHT_GRAY);
		
		ipResponse = new JLabel();
		ipResponse.setHorizontalAlignment(SwingConstants.CENTER);
		ipResponse.setOpaque(true);
		ipResponse.setForeground(Color.ORANGE);
		ipResponse.setBackground(Color.BLACK);
		
		ipServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ipResponse.setText(HttpPetition.ping(ipServerWrite.getText()+":4567",dataProperties));
				ipServerWrite.setText("");
				save();
			}
		});
		
		ipMongoWrite = new JTextField();
		ipMongoWrite.setHorizontalAlignment(SwingConstants.CENTER);
		ipMongoWrite.setBackground(Color.BLACK);
		ipMongoWrite.setForeground(Color.ORANGE);
		ipMongoWrite.setColumns(10);
		SpringLayout sl_ipAsign = new SpringLayout();
		sl_ipAsign.putConstraint(SpringLayout.WEST, ipServer, 31, SpringLayout.WEST, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.WEST, ipServerWrite, 0, SpringLayout.WEST, ipResponse);
		sl_ipAsign.putConstraint(SpringLayout.EAST, ipMongoWrite, 0, SpringLayout.EAST, ipResponse);
		sl_ipAsign.putConstraint(SpringLayout.WEST, ipResponse, 31, SpringLayout.WEST, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.EAST, ipResponse, 467, SpringLayout.WEST, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.NORTH, ipServer, 168, SpringLayout.NORTH, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.SOUTH, ipServer, 189, SpringLayout.NORTH, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.NORTH, ipMongoWrite, 84, SpringLayout.NORTH, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.WEST, ipMongoWrite, 264, SpringLayout.WEST, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.SOUTH, ipMongoWrite, 126, SpringLayout.NORTH, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.NORTH, ipServerWrite, 83, SpringLayout.NORTH, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.SOUTH, ipServerWrite, 127, SpringLayout.NORTH, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.EAST, ipServerWrite, 230, SpringLayout.WEST, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.NORTH, ipResponse, 238, SpringLayout.NORTH, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.SOUTH, ipResponse, 306, SpringLayout.NORTH, ipAsign);
		ipAsign.setLayout(sl_ipAsign);
		ipAsign.add(ipResponse);
		ipAsign.add(ipServerWrite);
		ipAsign.add(ipMongoWrite);
		ipAsign.add(ipServer);
		
		JButton ipMongo = new JButton(p.getProperty("conectIpMongo"));
		ipMongo.setBackground(Color.LIGHT_GRAY);
		sl_ipAsign.putConstraint(SpringLayout.WEST, ipMongo, 264, SpringLayout.WEST, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.EAST, ipServer, -34, SpringLayout.WEST, ipMongo);
		sl_ipAsign.putConstraint(SpringLayout.NORTH, ipMongo, 42, SpringLayout.SOUTH, ipMongoWrite);
		sl_ipAsign.putConstraint(SpringLayout.SOUTH, ipMongo, -49, SpringLayout.NORTH, ipResponse);
		sl_ipAsign.putConstraint(SpringLayout.EAST, ipMongo, 0, SpringLayout.EAST, ipResponse);
		ipMongo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String args[] = ipMongoWrite.getText().split(" ");
				String ip = "mongodb://" + args[0] + ":" + args[1] + "@" + args[2] + ":27017/";
				try {
					InetAddress address = InetAddress.getByName(args[2]);
			        address.getHostAddress();
			        
			        try (MongoClient mongoClient = MongoClients.create(ip)) {
				        boolean dbExists = false;
				        for (String dbName : mongoClient.listDatabaseNames()) {
				            if (dbName.equals(args[3])) {
				                dbExists = true;
				                break;
				            }
				        }

				        if (!dbExists) {
				            throw new IOException(p.getProperty("databaseNot"));
				        }

				        boolean collectionExists = false;
				        var database = mongoClient.getDatabase(args[3]);
				        for (String collectionName : database.listCollectionNames()) {
				            if (collectionName.equals(args[4])) {
				                collectionExists = true;
				                break;
				            }
				        }

				        if (!collectionExists) {
				            throw new IOException(p.getProperty("collectionNot"));
				        }
				        
				        dataProperties.setProperty("mongo", ip + " " + args[3] + " " + args[4]);
				        ipResponse.setText(p.getProperty("connected"));
				        ipMongoWrite.setText("");
				        save();
				    } catch (MongoSocketException e1) {
				        throw new MongoSocketException(p.getProperty("errorConecting") + args[2] + ": " + e1.getMessage(), null, e1.getCause());
				    }
				} catch (UnknownHostException e1) {
					ipResponse.setText(p.getProperty("wrongUrl"));
				} catch (IOException e2) {
					ipResponse.setText(p.getProperty("error") + e2.getMessage());
				}catch (MongoSecurityException e3) {
					ipResponse.setText(p.getProperty("wrongCredential"));
				} catch (Exception e4) {
					ipResponse.setText("ERROR: " + e4.getClass() + e4.getMessage());
				}
			    

			    
			}
		});
		ipAsign.add(ipMongo);
		
		JButton question = new JButton("?");
		question.setBackground(Color.CYAN);
		sl_ipAsign.putConstraint(SpringLayout.NORTH, question, 28, SpringLayout.NORTH, ipAsign);
		sl_ipAsign.putConstraint(SpringLayout.EAST, question, 0, SpringLayout.EAST, ipResponse);
		question.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        JOptionPane.showMessageDialog(ipAsign, p.get("ipInfo"), null, JOptionPane.INFORMATION_MESSAGE);
			}
		});
		ipAsign.add(question);
		
		
	}
	
	public JButton playerButton(String w) {
		JButton button = new JButton(w);
        
        button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] opciones = {
			            p.getProperty("ban"),
			            p.getProperty("kick"),
			            p.getProperty("kill")
			        };

			        int seleccion = JOptionPane.showOptionDialog(
			            null,
			            p.get("textBefore") + w + ":",
			            null,
			            JOptionPane.DEFAULT_OPTION,
			            JOptionPane.WARNING_MESSAGE,
			            null,
			            opciones,
			            opciones[0]
			        );

			        switch (seleccion) {
			            case 0 -> HttpPetition.sendCommand("ban " + w,dataProperties);
			            case 1 -> HttpPetition.sendCommand("kick " + w,dataProperties);
			            case 2 -> HttpPetition.sendCommand("kill " + w,dataProperties);
			            default -> {}
			        }
			}
		});
        
        
		return button;
	}
	
	public static List<String> fetchDeaths() {
		String[] parts = dataProperties.getProperty("mongo").split(" ");
        List<String> deaths = new ArrayList<>();
        try (MongoClient client = MongoClients.create(parts[0])) {
            MongoDatabase db = client.getDatabase(parts[1]);
            MongoCollection<Document> collection = db.getCollection(parts[2]);

            for (Document doc : collection.find()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Player: ").append(doc.getString("Player")).append("\n");
                sb.append("Time: ").append(doc.getString("Time")).append("\n");
                sb.append("Death: ").append(doc.getString("Death")).append("\n");

                Document loc = (Document) doc.get("Location");
                sb.append("Location: X=").append(loc.get("X"))
                  .append(" Y=").append(loc.get("Y"))
                  .append(" Z=").append(loc.get("Z")).append("\n");

                List<Document> inventory = (List<Document>) doc.get("Inventory");
                sb.append("Inventory:\n");
                for (Document item : inventory) {
                    sb.append(" - ").append(item.getString("Item"))
                      .append(" x").append(item.getInteger("Amount")).append("\n");

                    if (item.containsKey("Enchantments")) {
                        List<Document> ench = (List<Document>) item.get("Enchantments");
                        for (Document e : ench) {
                            sb.append("   Enchantment: ").append(e.getString("Name"))
                              .append(" Level ").append(e.get("Level")).append("\n");
                        }
                    }

                    if (item.containsKey("Durability")) {
                        Document dura = (Document) item.get("Durability");
                        sb.append("   Durability: ").append(dura.get("Current"))
                          .append("/").append(dura.get("Max")).append("\n");
                    }
                }
                deaths.add(sb.toString());
            }
        } catch (Exception e) {
            deaths.add("Error: " + e.getMessage());
        }

        return deaths;
    }
}
