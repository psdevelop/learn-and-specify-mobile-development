//package TaxiJavaMidletClass

// Сначала подключим классы, которые нам понадобятся для программы
// каждая программа на java начинается именно с этого.

import java.io.*;
//import java.io.Reader;
import com.mobile.util.*;
import java.lang.Thread;
import java.lang.Boolean;

import org.json.me.*;

import java.util.*;
//import java.util.;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import javax.microedition.media.*;
import javax.microedition.media.control.ToneControl;
import org.xmlpull.v1.*;
import org.kxml2.io.KXmlParser;
import org.bouncycastle.util.encoders.Base64;


// Теперь создадим стандартный рабочий класс J2ME, расширяющий класс
// MIDlet, именно этот класс является обязательным элементом J2ME программы

interface SocketReceiver    {

}

interface SocketTramsmitter {
    
}

public class TaxiJavaMidletClass extends MIDlet implements CommandListener {

      private Display display;
      private Command exit = new Command("Выход", Command.EXIT, 1);
      private Command Settings = new Command("Настройки...", Command.SCREEN, 1);
      private Command MainWindow = new Command("Работа", Command.SCREEN, 1);
      private Command ConnectWindow = new Command("Соединение...", Command.SCREEN, 1);
      private Command ConnectHTTPWindow = new Command("Работать...", Command.SCREEN, 1);
      private Form menu;
      private TextField login_tb;
      private TextField psw_tb;
      private MainFormMenuClass MainFormMenu;
      private SettingsFormMenuClass SettingsFormMenu;
      private ConnectionFormMenuClass ConnectionFormMenu;
      private AbortConnectionFormMenuClass AbortConnectionFormMenu;
      private SocketClient TDSocketClient;
      private HTTPClient TDHTTPClient;
      //private Calendar c;
      XMLParser prs;

	// Переопределим стандартные функции класса MIDlet: startApp(),
	// pauseApp() и destroyApp(boolean n) которые отвечают
	// соответственно за действия при запуске приложения, его
	// приостановке и выходе из // него. В нашем  случае переопределен
	// только startApp(), который содержит собственно всю программу
	// функция destroyApp() и pauseApp() в нашем случае не играют никакой
	// роли, однако их указание даже в пустом виде - обязательно!

	public TaxiJavaMidletClass() throws Exception {
            display = Display.getDisplay(this);
            MainFormMenu = new MainFormMenuClass(this,"Меню основного окна TDJavaClient");
            SettingsFormMenu = new SettingsFormMenuClass(this,"Меню окна настроек TDJavaClient");
            ConnectionFormMenu = new ConnectionFormMenuClass(this,"Меню окна соединения TDJavaClient");
            //ConnectionFormMenu.displayMenu();
            AbortConnectionFormMenu = new AbortConnectionFormMenuClass(this,"Меню окна обрыва соединения TDJavaClient");
            //AbortConnectionFormMenu.displayMenu();
            
        }

        public void ReceiveFromSocket(String action, String data)    {

        }

        public String GetCurrentStrTime()   {
            Calendar c = Calendar.getInstance();
            return c.get(Calendar.YEAR)+"-"+
                c.get(Calendar.MONTH)+"-"+
                c.get(Calendar.DAY_OF_MONTH)+" "+
                c.get(Calendar.HOUR)+":"+
                c.get(Calendar.MINUTE)+":"+
                c.get(Calendar.SECOND);
        }

	public void displayMenu() {
            menu = new Form("Панель входа"); 
            login_tb = new TextField("Введите позывной (имя): ","",30,TextField.ANY );
            psw_tb = new TextField("Введите пароль: ","",30,TextField.PASSWORD);
            try {
                login_tb.setString(this.getAppProperty("USER_LOGIN"));
            } catch (Exception e) {
                login_tb.setString("Не задан");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }

            try {
                psw_tb.setString(this.getAppProperty("HTTP1_PSW"));
            } catch (Exception e) {
                psw_tb.setString("");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }

            menu.append(login_tb);
            menu.append(psw_tb);
            menu.addCommand(exit);
            menu.addCommand(Settings);
            //menu.addCommand(MainWindow);
            //menu.addCommand(ConnectWindow);
            menu.addCommand(ConnectHTTPWindow);
            menu.setCommandListener(this);
            display.setCurrent(menu);
      	}

	public void commandAction(Command command, Displayable screen) {
            if (command == exit) {
                  destroyApp(false);
                  notifyDestroyed();
            } else if (command == Settings) {
                SettingsFormMenu.displayMenu();
            }
            else if (command == MainWindow) {
                display.setCurrent(MainFormMenu.MainForm);
                MainFormMenu.displayMenu();
            }
            else if (command == ConnectWindow)  {
                //display.setCurrent(ConnectionFormMenu.ConnectionForm);
                ConnectionFormMenu.displayMenu();
                TDSocketClient = new SocketClient(this,"www.java2s.com","80", login_tb.getString(), psw_tb.getString());
                TDSocketClient.startSocket();
            }
            else if (command == ConnectHTTPWindow)  {
                //MIDlet.
                byte[] coded = Base64.encode(psw_tb.getString().getBytes());
                String strCoded = new String(coded);
                TDHTTPClient = new HTTPClient(this,this.getAppProperty("SYS_ADDR_HTTP1")+
                        "index.php?action=get_messages&dest_id="+login_tb.getString()+
                        "&psw="+strCoded+"&charset="+this.getAppProperty("CHARSET"));
                //TDHTTPClient.startHTTPClient();
                TDSocketClient = new SocketClient(this, this.getAppProperty("SYS_ADDR_HTTP1"), "5050", login_tb.getString(), psw_tb.getString());
                TDSocketClient.startSocket();
            }
      	}

    public class MainFormMenuClass implements CommandListener {
        private Display display;
        TaxiJavaMidletClass midlet;
        private Command ToStartMenu = new Command("Главное меню", Command.EXIT, 1);
        private Command FunctionMenu = new Command("Функции", Command.SCREEN, 1);
	private Form MainForm;

        public MainFormMenuClass(TaxiJavaMidletClass midlet, String Title) {
            this.midlet = midlet;
            display = Display.getDisplay(midlet);
        }
                
        public void displayMenu() {
            MainForm = new Form("Основное окно TDJavaClient"); 
            MainForm.addCommand(ToStartMenu);
            MainForm.addCommand(FunctionMenu);
            MainForm.setCommandListener(this);
            display.setCurrent(MainForm);
	}   

	public void commandAction(Command command, Displayable screen) {
            if (command == ToStartMenu) {
                display.setCurrent(midlet.menu);
            } else if (command == FunctionMenu) {

            }
	}
            
    };
    
    public class ConnectionFormMenuClass implements CommandListener {
        private Display display;
        TaxiJavaMidletClass midlet;
        public StringItem messageLabel;
        public TextField sale_rep_tf;
        public TextField active_order_tf;
        private Command ToStartMenu = new Command("Главное меню", Command.EXIT, 1);
        private Command AbortConnectMenu = new Command("Прервать", Command.SCREEN, 1);
	private Command SaleReportMenu = new Command("Отчитаться", Command.SCREEN, 1);
        private Command AcceptOrderMenu = new Command("Принять заказ", Command.SCREEN, 1);
        private Command DenyOrderMenu = new Command("Отказаться", Command.SCREEN, 1);
        private Command SelfOrderMenu = new Command("С руки", Command.SCREEN, 1);
        private Command QueuePositionMenu = new Command("Я в очереди", Command.SCREEN, 1);
        private Form ConnectionForm;

        public ConnectionFormMenuClass(TaxiJavaMidletClass midlet, String Title) {
            this.midlet = midlet;
            display = Display.getDisplay(midlet);
            ConnectionForm = new Form("Осуществляется соединение");
            messageLabel = new StringItem(null, "Connecting...\nPlease wait.");
            sale_rep_tf = new TextField("Отчет по заявке: ","",30,TextField.ANY );
            active_order_tf = new TextField("Выполняемая заявка: ","",30,TextField.ANY );
            ConnectionForm.append(sale_rep_tf);
            ConnectionForm.append(active_order_tf);
            ConnectionForm.append(messageLabel);
            ConnectionForm.addCommand(ToStartMenu);
            ConnectionForm.addCommand(AbortConnectMenu);
            ConnectionForm.addCommand(SaleReportMenu);
            ConnectionForm.addCommand(AcceptOrderMenu);
            ConnectionForm.addCommand(DenyOrderMenu);
            ConnectionForm.addCommand(SelfOrderMenu);
            ConnectionForm.addCommand(QueuePositionMenu);
            ConnectionForm.setCommandListener(this);
        }
                
        public void displayMenu() { 
            display.setCurrent(ConnectionForm);
	}

	public void commandAction(Command command, Displayable screen) {
            if (command == ToStartMenu) {
                midlet.TDHTTPClient.activeState = false;
                display.setCurrent(midlet.menu);
            } else if (command == AbortConnectMenu) {
                midlet.TDHTTPClient.activeState = false;
                display.setCurrent(midlet.menu);
            }
	}
            
    };

    public class AbortConnectionFormMenuClass implements CommandListener {
        private Display display;
        TaxiJavaMidletClass midlet;
        public StringItem messageLabel;
        private Command ToStartMenu = new Command("Главное меню", Command.EXIT, 1);
        private Command RetryConnectMenu = new Command("Повторить соединение", Command.SCREEN, 1);
	private Form AbortConnectionForm;

        public AbortConnectionFormMenuClass(TaxiJavaMidletClass midlet, String Title) {
            this.midlet = midlet;
            display = Display.getDisplay(midlet);
            AbortConnectionForm = new Form("Неудачное соединение/разрыв");
            messageLabel = new StringItem(null, "Ожидание очередной попытки...\n");
            AbortConnectionForm.append(messageLabel);
            AbortConnectionForm.addCommand(ToStartMenu);
            AbortConnectionForm.addCommand(RetryConnectMenu);
            AbortConnectionForm.setCommandListener(this);
        }

        public void displayMenu(int badAttempts) {
            if (badAttempts>5) {
                this.messageLabel.setText("Количество попыток превысило 5, "+
                    "цикл соединений отключен!\n"+
                    this.messageLabel.getText());
                midlet.TDHTTPClient.activeState = false;
            }
            display.setCurrent(AbortConnectionForm);
	}

	public void commandAction(Command command, Displayable screen) {
            if (command == ToStartMenu) {
                midlet.TDHTTPClient.activeState = false;
                display.setCurrent(midlet.menu);
            } else if (command == RetryConnectMenu) {
                midlet.TDHTTPClient.activeState = true;
                midlet.TDHTTPClient.badAttempts = 0;
                midlet.TDHTTPClient.fastConnection = true;
                display.setCurrent(midlet.ConnectionFormMenu.ConnectionForm);
                midlet.ConnectionFormMenu.messageLabel.setText(
                    "Ожидание очередной попытки...\nЖдите...");
            }
	}

    };

    public class SettingsFormMenuClass implements CommandListener {
        private Display display;
        TaxiJavaMidletClass midlet;
        private Command ToStartMenu = new Command("Главное меню", Command.EXIT, 1);
        private Command SaveMenu = new Command("Выйти", Command.SCREEN, 1);
	private TextField sys_adr_http1_tf;
        private TextField sys_adr_http2_tf;
        private TextField http1_psw_tf;
        private TextField http2_psw_tf;
        private TextField serv1_ip_tf;
        private TextField serv2_ip_tf;
        private TextField serv1_psw_tf;
        private TextField serv2_psw_tf;
        private TextField serv1_port_tf;
        private TextField serv2_port_tf;
	private Form SettingsForm;

        public SettingsFormMenuClass(TaxiJavaMidletClass midlet, String Title) {
            this.midlet = midlet;
            display = Display.getDisplay(midlet);
            
            SettingsForm = new Form("Окно настроек TDJavaClient");
            
            sys_adr_http1_tf = new TextField("Адрес инф. скрипта 1: ","",130,TextField.ANY );
            sys_adr_http2_tf = new TextField("Адрес инф. скрипта 2: ","",130,TextField.ANY );
            http1_psw_tf = new TextField("Пароль инф. скрипта 1: ","",30,TextField.PASSWORD );
            http2_psw_tf = new TextField("Пароль инф. скрипта 2: ","",30,TextField.PASSWORD );
            serv1_ip_tf = new TextField("IP адрес сервера 1: ","",30,TextField.ANY );
            serv2_ip_tf = new TextField("IP адрес сервера 2: ","",30,TextField.ANY );
            serv1_psw_tf = new TextField("Пароль сервера 1: ","",30,TextField.PASSWORD );
            serv2_psw_tf = new TextField("Пароль сервера 2: ","",30,TextField.PASSWORD );
            serv1_port_tf = new TextField("Порт сервера 1: ","",30,TextField.ANY );
            serv2_port_tf = new TextField("Порт сервера 2: ","",30,TextField.ANY );

            SettingsForm.append(sys_adr_http1_tf);
            SettingsForm.append(http1_psw_tf);
            SettingsForm.append(sys_adr_http2_tf);
            SettingsForm.append(http2_psw_tf);
            SettingsForm.append(serv1_ip_tf);
            SettingsForm.append(serv1_psw_tf);
            SettingsForm.append(serv1_port_tf);
            SettingsForm.append(serv2_ip_tf);
            SettingsForm.append(serv2_psw_tf);
            SettingsForm.append(serv2_port_tf);
            SettingsForm.addCommand(ToStartMenu);
            SettingsForm.addCommand(SaveMenu);
            SettingsForm.setCommandListener(this);
        }
                
        public void displayMenu() {
            try {
                sys_adr_http1_tf.setString(midlet.getAppProperty("SYS_ADDR_HTTP1"));
            } catch (Exception e) {
                sys_adr_http1_tf.setString("Не определено");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }
            
            try {
                sys_adr_http2_tf.setString(midlet.getAppProperty("SYS_ADDR_HTTP2"));
            } catch (Exception e) {
                sys_adr_http2_tf.setString("Не определено");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }
            
            try {
                http1_psw_tf.setString(midlet.getAppProperty("HTTP1_PSW"));
            } catch (Exception e) {
                http1_psw_tf.setString("Не определено");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }
            
            try {
                http2_psw_tf.setString(midlet.getAppProperty("HTTP2_PSW"));
            } catch (Exception e) {
                http2_psw_tf.setString("Не определено");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }
            
            try {
                serv1_ip_tf.setString(midlet.getAppProperty("IP1"));
            } catch (Exception e) {
                serv1_ip_tf.setString("Не определено");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }
            
            try {
                serv2_ip_tf.setString(midlet.getAppProperty("IP2"));
            } catch (Exception e) {
                serv2_ip_tf.setString("Не определено");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }
            
            try {
                serv1_psw_tf.setString(midlet.getAppProperty("IP1_PSW"));
            } catch (Exception e) {
                serv1_psw_tf.setString("Не определено");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }
            
            try {
                serv2_psw_tf.setString(midlet.getAppProperty("IP2_PSW"));
            } catch (Exception e) {
                serv2_psw_tf.setString("Не определено");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }
            
            try {
                serv1_port_tf.setString(midlet.getAppProperty("IP1_PORT"));
            } catch (Exception e) {
                serv1_port_tf.setString("Не определено");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }
            
            try {
                serv2_port_tf.setString(midlet.getAppProperty("IP2_PORT"));
            } catch (Exception e) {
                serv2_port_tf.setString("Не определено");
                //showAlert(e.getMessage());
   		//SettingsForm.append(e.getMessage()+"!\n");
            }
			
            
            display.setCurrent(SettingsForm);
	}   

	public void commandAction(Command command, Displayable screen) {
            if (command == ToStartMenu) {
                display.setCurrent(midlet.menu);
            } else if (command == SaveMenu) {
                display.setCurrent(midlet.menu);
            }
	}
            
    };

    public class SocketClient implements Runnable {
        TaxiJavaMidletClass midlet;
        private Display display;
        private String server;
        private String port;
        private String RequestAddress;
        String db;
        String login;
        String psw;
        String clientId = "";
        boolean requestSuccess;
        boolean activeState;
        public int badAttempts=0;
        public boolean fastConnection = false;
        int counter = 1;
        Thread t;
        boolean SocketHasError = false;
        Hashtable sendList;
        Vector sendVector;
        boolean lastSendindNOHasASK = false;
        boolean autorized = false;
        InputStream is = null;
        OutputStream os = null;
        SocketConnection socket = null;
        boolean inWorking = false;
        boolean inWorkingOnHand = false;
        String activeOrderID = "";
        boolean playTones = false;

        public SocketClient(TaxiJavaMidletClass midlet, String server, String port, String login_name, String psw_str) {
            this.midlet = midlet;
            this.server = server;
            this.port = port;
            this.badAttempts = 0;
            this.login = login_name;
            this.psw = psw_str;
            display = Display.getDisplay(midlet);
            this.fastConnection = false;
            this.lastSendindNOHasASK = false;
            this.sendVector = new Vector();
            String v_obj = "{\"command\":\"connect_attempt\","+
                    "\"msg_end\":\"ok\"}";
            this.sendVector.addElement(v_obj);
            v_obj = "{\"command\":\"autorization\","+
                    "\"login\":\"18\",\"psw\":\"12345\","+
                    "\"msg_end\":\"ok\"}";
            this.sendVector.addElement(v_obj);
            this.playTones = false;
        }

        public void TransmitToSockset(String action, String data)   {

        }
        
        public void startSocket() {
            display.setCurrent(midlet.ConnectionFormMenu.
                    ConnectionForm);
            t = new Thread(this);
            t.start();
        }

        public void playMTones(int counter)  {
          if (this.playTones)   {
            for(int i=0;i<=counter;i++) {
              try   {
                Manager.playTone(ToneControl.C4+12, 4000, 100);
              } catch(Exception exm)   {

              }
            }
          }
        }

        public void playNotes() {
            if (this.playTones)   {
                byte[] Nots={
                    ToneControl.VERSION, 1, //версия используемого атрибута
                    ToneControl.TEMPO, 60, //темп мелодии. Переменная speed = 5-127
                    ToneControl.BLOCK_START, 0, //начало блока 0
                    ToneControl.C4, 4,          //Нота До продолжительностью 4 (можно от 2 до 16)
                    ToneControl.SILENCE, 4, //Пауза продолжительностью 4
                    ToneControl.C4+1, 4,          //Нота До# продолжительностью 4
                    ToneControl.SILENCE, 4, //Пауза продолжительностью 4
                    ToneControl.C4+2, 4,          //Нота Ре продолжительностью 4
                    ToneControl.SILENCE, 4, //Пауза продолжительностью 4
                    ToneControl.C4+3, 4,
                    ToneControl.SILENCE, 4,
                    ToneControl.C4+4, 4,
                    ToneControl.SILENCE, 4,
                    ToneControl.C4+5, 4,
                    ToneControl.SILENCE, 4,
                    ToneControl.C4+6, 4,
                    ToneControl.SILENCE, 4,
                    ToneControl.C4+7, 4,
                    ToneControl.SILENCE, 4,
                    ToneControl.C4+8, 4,
                    ToneControl.SILENCE, 4,
                    ToneControl.C4+9, 4,
                    ToneControl.SILENCE, 4,
                    ToneControl.C4+10, 4,
                    ToneControl.SILENCE, 4,
                    ToneControl.C4+11, 4,
                    ToneControl.SILENCE, 4,
                    ToneControl.C4+12, 4,     //Нота До следующей октавы
                    ToneControl.BLOCK_END, 0, //конец блока 0
                    ToneControl.PLAY_BLOCK, 0 }; //воспроизведение блока 0

                    try {
                        Player player =
                          Manager.createPlayer(Manager.TONE_DEVICE_LOCATOR);
                        player.realize();
                        ToneControl tc1=(ToneControl)player.getControl("ToneControl");
                        //Подключаем последовательность нот, указанных в массиве Nots
                        tc1.setSequence(Nots);
                        player.start();
                    } catch (Exception mex) {

                    }
            }
        }

        public void proceedInputInstruction(JSONObject input_json)   {
            Enumeration ken = input_json.keys();

            if (input_json.has("command")) {
                try {
                    String commandName =
                        input_json.getString("command");

                    if (commandName.equalsIgnoreCase("autorization_ok")) {
                        this.autorized = true;
                        this.clientId =
                           input_json.getString("client_id");
                    }
					else if (commandName.equalsIgnoreCase("autorization_unsucc"))    {
						//Неудачная авторизация
                    }
                    else if (commandName.equalsIgnoreCase("take_order"))    {
						//Предлагается заказ
                    }
					else if (commandName.equalsIgnoreCase("order_occuped"))    {
						//Ничего не делать пока
                    }
					else if (commandName.equalsIgnoreCase("order_is_your"))    {
						//Заказ закреплен за клиентом
                    }
					else if (commandName.equalsIgnoreCase("go_on_hand"))    {
						//Разрешено работать с руки
                    }
					else if (commandName.equalsIgnoreCase("onhand_block"))    {
						//Запрещено работать с руки
                    }
					else if (commandName.equalsIgnoreCase("order_cancel"))    {
						//Диспетчер сделал отмену
                    }
					else if (commandName.equalsIgnoreCase("accept_working_abort"))    {
						//Диспетчер подтвердил отмену водителем
                    }
					else if (commandName.equalsIgnoreCase("order_close"))    {
						//Диспетчер подтвердил закрытие заявки
                    }
					else if (commandName.equalsIgnoreCase("error_order_close"))    {
						//Ошибка закрытия заявки, неверная сумма, но ее можно заранее
						//проверять тут
                    }
					else if (commandName.equalsIgnoreCase("remove_from_line"))    {
						//Диспетчер подтвердил снятие с линии
                    }
					else if (commandName.equalsIgnoreCase("keep_on_line"))    {
						//Диспетчер запретил снятие с линии
                    }
					else if (commandName.equalsIgnoreCase("go_on_rest"))    {
						//Диспетчер подтвердил уход на перерыв
                    }
					else if (commandName.equalsIgnoreCase("not_on_rest"))    {
						//Диспетчер запретил уход на перерыв
                    }
					else if (commandName.equalsIgnoreCase("queue_answer"))    {
						//Информация о местоположении в очереди
                    }
                    else    {
                        //Unsupported command
                    }

                }
                catch (Exception ex)    {
                    midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+
                      "\nОшибка обработки входящей команды! "
                      +ex.getMessage());
                }
            }
            //String key_name;
            //while(ken.hasMoreElements())    {
            //key_name = ken.nextElement().toString();
            //}
        }

        public void stopSocket()    {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException ex1) {
            }
            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (IOException ex1) {
            }
            try {
                if (socket != null) {
                    socket.close();
                    socket = null;
                }
            } catch (IOException ex1) {
            }
        }

        public void run() {
        
        while (true)    {
        midlet.ConnectionFormMenu.messageLabel.setText("");

        if (!this.fastConnection)    {

        try {
            //String server = serverName.getString();
            //String port = serverPort.getString();
            String name = "socket://127.0.0.1:5050";//192.168.112.1:8080";//127.0.0.1:4444";
            socket = (SocketConnection)Connector.open(name, Connector.READ_WRITE);
            midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+"\nСокет инициализирован!"+counter);
            os = socket.openOutputStream();
            is = socket.openInputStream();
            this.fastConnection = true;
            this.autorized = false;
            this.clientId = "";
            this.badAttempts=0;
            playMTones(4);
            
        } catch (Exception ex) {
            if(this.badAttempts<=5) {
            midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+"\nСокет не инициализирован!"+counter+ex.getMessage());
            }   else
            {
                midlet.ConnectionFormMenu.messageLabel.setText(
                    "Сокет не инициализирован за более чем 5 попыток!"+
                    " Выйдите из программы и попробуйте позже. Ошибка:"+
                    ex.getMessage());
                playMTones(4);
                //playNotes();
                stopSocket();
                t.interrupt();
                break;
            }
                this.badAttempts++;
                this.fastConnection=false;
                continue;
            }
        }
        else
        {
          try   {
            //os = socket.openOutputStream();
            //is = socket.openInputStream();
          } catch(Exception ex)   {
            midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+
                    "\nПотоки не инициализированы!"+counter+ex.getMessage());
          } 
        }

        try {
            // Send a message to the server
            
            if (!this.lastSendindNOHasASK)  {
              if(!this.sendVector.isEmpty())    {
                String request_str =
                  (String)this.sendVector.firstElement();
                os.write(request_str.getBytes());
                os.flush();
                midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+
                    "\nJSON trasmitted!");//+ex.getMessage());
                this.lastSendindNOHasASK = true;
              }
            }

                StringBuffer sb = new StringBuffer();
                int c = -2;
                int cnt = 1, prev_cnt=1;

                while (((c = is.read()) != '\n') && (c != -1) ) { //&&(c>0)&& (c!=0)
                    //if (((char)c!='0'))
                        sb.append((char)c);
                    try {
                        t.sleep(10);
                    } catch (Exception ex) {

                    }

                    if ((c==-1)||(is.available()<=0))
                        break;

                    //midlet.ConnectionFormMenu.messageLabel.setText(
                    //"Message received - " + sb.toString()+"--"+c+"--"+(char)(int)c+"--"+cnt+"--");
                    cnt++;
                }
                
                // Display message to user
                midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+
                    "Message received - " + sb.toString());

            String reply = sb.toString();//new String(buf, 0, total);

            //midlet.ConnectionFormMenu.messageLabel.setText(
            //   midlet.ConnectionFormMenu.messageLabel.getText()+"\nДанные получены!");

            if (reply.indexOf("\"ask\":\"ok\"}")==-1)
                midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+
                    "\nПередаю ASK-OK!");
            try {
            JSONObject json = new JSONObject(reply);
            proceedInputInstruction(json);
            //midlet.ConnectionFormMenu.messageLabel.setText(
            //   midlet.ConnectionFormMenu.messageLabel.getText()+
            //   key_name+"="+json.getString(key_name)+",");
            

            if (reply.indexOf("\"ask\":\"ok\"}")==-1)    {
            try {
                String request = "{\"ask\":\"ok\"}";
                os.write(request.getBytes());
                os.flush();
            } catch(Exception ex)   {
                midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+
                    " \nНеудачная передача ASK-OK! "+counter+ex.getMessage());
                this.fastConnection = false;
            }
            }   else    {
                if(!this.sendVector.isEmpty()&&
                        this.lastSendindNOHasASK)
                    this.sendVector.removeElementAt(0);
                this.lastSendindNOHasASK = false;
            }
            
            } catch(Exception ex) {
                midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+
                    " \nНеудачный парсинг JSON! "+ex.getMessage());
            }

            //midlet.ConnectionFormMenu.messageLabel.setText(
            //   midlet.ConnectionFormMenu.messageLabel.getText()+reply);
        }
        catch (ConnectionNotFoundException err)  {
           midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+"\nПотеряно соединение! "+err.getMessage());
           this.fastConnection = false;
        }
        catch (IOException ex) {
            midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+"\nСбой обмена данными сокет-соединения! "+ex.getMessage());
            this.fastConnection = false;
        }
        catch (Exception aex)    {
            midlet.ConnectionFormMenu.messageLabel.setText(
                    midlet.ConnectionFormMenu.messageLabel.getText()+"\nОбрыв операций с сокетом! "+aex.getMessage());
            this.fastConnection = false;
        }

        if (!this.fastConnection) {
            // Close open streams and the socket
            stopSocket();
          }
        else
        {
            
        }
        
        counter++;
        try {
            t.sleep(5000);
        } catch (Exception ex)  {

        }

        //break;
       
        }//Конец главного цикла обмена-подключения-перепоключения

     }
    
    }
    
	public class HTTPClient implements Runnable {
            TaxiJavaMidletClass midlet;
            private Display display;
            private String RequestAddress;
            String db;
            String user;
            String pwd;
            boolean requestSuccess;
            boolean activeState;
            public int badAttempts;
            public boolean fastConnection;
            public String WIN1251_TO_UNICODE =
                "\u0402\u0403\u201a\u0453\u201e\u2026\u2020\u2021" +
                "\u20ac\u2030\u0409\u2039\u040a\u040c\u040b\u040f\u0452"+
                "\u2018\u2019\u201c\u201d\u2022\u2013\u2014\ufffd"+
                "\u2122\u0459\u203a\u045a\u045c\u045b\u045f\u00a0"+
                "\u040e\u045e\u0408\u00a4\u0490\u00a6\u00a7\u0401"+
                "\u00a9\u0404\u00ab\u00ac\u00ad\u00ae\u0407\u00b0\u00b1"+
                "\u0406\u0456\u0491\u00b5\u00b6\u00b7\u0451\u2116"+
                "\u0454\u00bb\u0458\u0405\u0455\u0457\u0410\u0411"+
                "\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u0419\u041a"+
                "\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422"+
                "\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a"+
                "\u042b\u042c\u042d\u042e\u042f\u0430\u0431\u0432\u0433"+
                "\u0434\u0435\u0436\u0437\u0438\u0439\u043a\u043b"+
                "\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443"+
                "\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c"+
                "\u044d\u044e\u044f";

            public HTTPClient(TaxiJavaMidletClass midlet, String RequestAddress) {
                  this.midlet = midlet;
                  this.RequestAddress = RequestAddress;
                  display = Display.getDisplay(midlet);
                  this.activeState = false;
                  Thread t = new Thread(this);
                  t.start();
                  badAttempts = 0;
                  fastConnection = false;
            }

            public void startHTTPClient() {
                  display.setCurrent(midlet.ConnectionFormMenu.ConnectionForm);
                  badAttempts = 0;
                  this.activeState = true;
                  fastConnection = true;
            }

            public void run() {
                while(true) {
                    if (this.activeState)   {
                        midlet.ConnectionFormMenu.messageLabel.setText(
                            "Осуществляется соединение...\n");
                        this.requestSuccess = false;
                        String httpAnswer = ReqServerFromClient(this.RequestAddress);

                        if (this.requestSuccess) {
                            midlet.ConnectionFormMenu.messageLabel.setText(
                                httpAnswer+" "+midlet.GetCurrentStrTime());
                            midlet.ConnectionFormMenu.displayMenu();
                        }   else    {
                            badAttempts++;
                            midlet.AbortConnectionFormMenu.messageLabel.setText(
                                httpAnswer+" "+midlet.GetCurrentStrTime());
                            midlet.AbortConnectionFormMenu.displayMenu(badAttempts);
                        }
                    }

                    try
                    {
                        for (int i=0;i<12;i++)
                            if (!fastConnection)
                                Thread.sleep(5000);
                    }
                        catch (InterruptedException e)
                    {
                        badAttempts++;
                        midlet.AbortConnectionFormMenu.messageLabel.setText(
                            "Ошибка задержки цикла опроса сервера!"+" "+midlet.GetCurrentStrTime());
                        midlet.AbortConnectionFormMenu.displayMenu(badAttempts);
                        //break;
                    }

                    fastConnection = false;

                }
            }

            private int convert(int ch) {// ch - код символа  в виндовой(1251) кодировке
                return (ch < 128) ? ch : this.WIN1251_TO_UNICODE.charAt(ch-128);
            }

	    //GetRequest = "http://www.websource.ru/index.php?par1=val1&...&parN=valN"  
            public String ReqServerFromClient(String GetRequest) {
   		String answer = "";  
   		HttpConnection hConnect = null;  
   		InputStream ReadData = null;


   		try {  
       		hConnect = (HttpConnection) Connector.open(GetRequest, Connector.READ_WRITE, true);  
       		//устанавливаем тип и свойства запроса на сервер  
       		hConnect.setRequestMethod(HttpConnection.GET);  
       		hConnect.setRequestProperty("Connection", "close");  
       		//hConnect.setRequestProperty("User-Agent", "Connect (MIDP-2.0: J2ME)"); 
                //hConnect.setRequestProperty("User-Agent","Profile/MIDP-1.0, Configuration/CLDC-1.0");
       		//hConnect.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
       		hConnect.setRequestProperty("Accept", "text/plain");  
       		//смотрим ответ сервера  
       		if (hConnect.getResponseCode() == HttpConnection.HTTP_OK) {  
         		ReadData = hConnect.openInputStream();
                        //DataOutputStream dos = new DataOutputStream(ReadData);
         		short ch = 4096;  
         		int len = 0;  
         		byte[] buff = new byte[ch];  
         		do {  
           			len = ReadData.read(buff);
                                //ReadData.r

           		if (len > 0) {
                                for (int i=0;i<len;i++) {
                                    //if((buff[i]>=192)&&(buff[i]<=255))
                                    //    buff[i]=(byte)(buff[i]+848);
                                    //buff[i]=0;//(byte)convert((int)(buff[i]));
                                }
                                String receive_str = new String(buff, 0, len);
                                String decode_str = "";
                                for (int i=0;i<receive_str.length();i++) {
                                    decode_str += (char)convert((int)receive_str.charAt(i));
                                }
              			answer += decode_str;  
              				}  
            		} while (len > 0);  
                        this.requestSuccess = true;
         	} else {  
           		answer = "Ответ сервера не 200 "+ hConnect.getResponseCode();  
         	}  
       		} catch (ConnectionNotFoundException cne) {  
         		answer = "Нет доступа к серверу: "+cne.getMessage();  
       		} catch (ClassCastException e) {  
         		answer = "Некорректный запрос к серверу: "+e.getMessage();  
       		} catch (IOException ioe) {  
         		answer = "Ошибка при соединении с сервером: "+ ioe.getMessage();  
       		} catch (SecurityException e) {  
         		answer = "Сетевой запрос отменен пользователем!";  
       		} catch (Exception a) {  
         		answer = "Ошибка приложения: "+	a.getMessage();  
       		} finally {  
         		try {  
           			ReadData.close();  
           			ReadData = null;  
         		} catch (Exception a) {  
         		}  
         		try {  
           			hConnect.close();  
           			hConnect = null;  
         		} catch (Exception a) {  
         		}  
       		}  
     		return answer;  
            }

            /* Display Error On screen*/
            private void showAlert(String err) {
                  Alert a = new Alert("");
                  a.setString(err);
                  a.setTimeout(Alert.FOREVER);
                  display.setCurrent(a);
            } 
      };

      public class XMLParser implements Runnable {
            TaxiJavaMidletClass midlet;
            private Display display;
 
            public XMLParser(TaxiJavaMidletClass midlet) {
                  this.midlet = midlet;
                  display = Display.getDisplay(midlet);
            }

            public void start() {
                  Thread t = new Thread(this);
                  t.start();
            }

            public void run() {
                  
            }

            public void parse (InputStream in)
		{
  		try
  			{
    			XmlPullParser xr = new KXmlParser();
    			xr.setInput (new InputStreamReader (new DataInputStream (in)));

    			do
    				{
      				try {
                        			xr.next();
					}
				catch (Exception a) {  
         		        		//answer = "Ошибка приложения: "+	a.getMessage();  
       					}
      				if (xr.getEventType() == XmlPullParser.START_TAG)
      					{
        					String tagName = xr.getName();
        					if (tagName.equals ("metro"))
        						{
          						String metroName =xr.getAttributeValue (null, "name");
          						String metroLines =
          						xr.getAttributeValue (null, "lines");
        						}
        					else if (tagName.equals ("line"))
        						{
          						String lineID =xr.getAttributeValue (null, "id");
          						String lineName =xr.getAttributeValue (null, "name");
        						}
        					else if (tagName.equals ("station"))
        						{
          						String stationID =xr.getAttributeValue (null, "id");
          						String stationName =xr.getAttributeValue (null, "name");
        						}
      					}
      				else if (xr.getEventType() == XmlPullParser.END_TAG)
      					{
        				String tagName = xr.getName();
        				if (tagName.equals ("metro"))
        					{
          					//...
        					}
        				else if (tagName.equals ("line"))
        					{
          					//...
        					}
        				else if (tagName.equals ("station"))
        					{
          					//...
        					}
      					}
    				}
    			while (xr.getEventType() != XmlPullParser.END_DOCUMENT);
  			}
  		catch (XmlPullParserException ex)
    			{
      			//...
    			}
  		}

	    /* Display Error On screen*/
            private void showAlert(String err) {
                  Alert a = new Alert("");
                  a.setString(err);
                  a.setTimeout(Alert.FOREVER);
                  display.setCurrent(a);
            }
	}; 

	public void destroyApp(boolean d) { notifyDestroyed(); }
	public void pauseApp(){}
	public void startApp()
		{
		displayMenu();
	}
}