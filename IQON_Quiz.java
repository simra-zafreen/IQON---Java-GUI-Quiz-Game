import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import javax.swing.JOptionPane;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

/**GUIQuiz is a JavaFX application for a timed quiz.
 * Tt reads questions from a fileal and allows the user to select an answer.
 * It maintains a leaderboard while calculating the scores.
 */

public class IQON_Quiz extends Application
{
    private String username;
    private int timeLeft = 300;                                                                         // Countdown from 5 mins
    private Timer timer = new Timer();                                                                  // Initialize Timer
    private Label timerLabel = new Label("Time Left: 5:00");                                       // Timer UI label
    private int score = 0;                                                                              //Score coounter
    private int currentQuestionIndex = 0;                                                               //Index to track which question the user is on
    private String selectedAnswer = null;                                                               //Stores the currently selected answer
    private String level;
    private MediaPlayer mPlayer;
    ;


    Stage window; 
    Scene scene1, scene2, scene7;                                                                       //The different scenes
    GridPane gp = new GridPane();                                                                       //Layout for arranging UI elements

    /*Returns a background object with the background image.
     * Used to apply a background to the scenes.
     */
    private Background getbackground() 
    {
        Image backgroundImage = new Image("resources/background.png"); 
        BackgroundImage background = new BackgroundImage(backgroundImage,BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,new BackgroundSize(100, 100, true, true, true, true));
        return new Background(background);
    }


    /*Timer using Threads */
    private void startTimer() 
    {
        Thread timerThread = new Thread(() -> {
            while (timeLeft > 0) {
                try 
                {
                    Thread.sleep(1000); // Wait 1 second
                } 
                catch (InterruptedException e) 
                {
                    return; // Exit if thread is interrupted
                }

                timeLeft--;
                int minutes = timeLeft / 60;
                int seconds = timeLeft % 60;
                String timeFormatted = String.format("Time Left: %d:%02d", minutes, seconds);

                Platform.runLater(() -> {
                    timerLabel.setText(timeFormatted);
                    timerLabel.setStyle("-fx-text-fill: white;");
                });
            }

        });

        timerThread.setDaemon(true); // Allows app to close even if thread is running
        timerThread.start();
    }



    /*Defines the appearance and functionality 
     * of the level selection buttons
     */
    private Button leveloption(String label, String fileName, Button[] selected, String defaultStyle) 
    {
        Button button = new Button(label);
        currentQuestionIndex = 0;                                                                      //Resets question to first one
        score = 0;                                                                                     // Resets score to 0
        button.setFont(new Font("Baskerville Old Face", 24));
        button.setPrefWidth(150);
        
        String selectedStyle = "-fx-background-color: black; -fx-border-color: black;-fx-border-width: 2px; -fx-border-radius: 5px;";            //Style when button is clicked on (CSS)

        button.setStyle(defaultStyle);
    
        button.setOnAction(e ->                                                                   //Functionality of buttons
        {
            level = fileName;                                                                    //Choose questions from specified filename (level used to store files)
            if (selected[0] != null) 
            {
                selected[0].setStyle(defaultStyle);                                             //Reset preiously selected button
            }
            button.setStyle(selectedStyle);                                                     //Set style of button when clicked on
            selected[0] = button;                                                               // Store button as currently selected one
        
            
            PauseTransition pause = new PauseTransition(Duration.seconds(0.3));                //Pause for 0.3 seconds
            pause.setOnFinished(ee -> 
            {
                startTimer();                                                                  //When time is up, start timer and display next scene
                showScene2();
            });
            pause.play();
        
        });
        return button;
    }



    /*Plays chosen audio file */
    private void music(String file) 
    {
        if (mPlayer != null)                                                                //Stop currently playing audio file
        {
            mPlayer.stop(); 
        }

        Media music = new Media(new File(file).toURI().toString());                         //Converts URI to string
        mPlayer = new MediaPlayer(music);           
        mPlayer.play();
    }
    
        
    

/**Setup for the initial user input
 * where the player enters their username 
 * and views the rules.
 */

    @Override
    public void start(Stage stage) throws IOException 
    {
        window=stage; 
        music("resources/audio.mp3");

        //UI elements setup
        Button press = new Button("Start Game!");
        press.setFont(new Font("Baskerville Old Face",35));
        press.setTextFill(Color.WHITE);
        press.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, CornerRadii.EMPTY, Insets.EMPTY))); 
        press.setPrefWidth(300);
        

        Label label1 =new Label("Enter Username");
        label1.setFont(new Font("Baskerville Old Face",24));
        label1.setTextFill(Color.WHITE);
        Color bckColor = Color.CADETBLUE;
        Color brdrColor = Color.CADETBLUE; 
        BorderStrokeStyle style = BorderStrokeStyle.SOLID;
        label1.setBackground(new Background(new BackgroundFill(bckColor, new CornerRadii(0), Insets.EMPTY)));
        label1.setPadding(new Insets(1));
        label1.setBorder(new Border(new BorderStroke(brdrColor,style,new CornerRadii(0),BorderStroke.THICK)));
        

        Label rules = new Label("Rules: \n-The quiz consists of 10 questions.\n" + 
                                        "-You have 5 minutes to complete the entire quiz.\n" + 
                                        "-Each correct answer awards 1 point.\n" + 
                                        "-You can select only one answer per question.\n" + 
                                        "-No external help or cheating is allowed.");
        rules.setFont(new Font("Baskerville Old Face",18));
        rules.setTextFill(Color.WHITE);

        Label disclaimer = new Label("NOTE: Selected answers will be highlighted in red.");
        disclaimer.setFont(new Font("Baskerville Old Face", 19));
        disclaimer.setTextFill(Color.LIGHTCORAL);

        
        TextField tf = new TextField();
        tf.setPrefWidth(200);
        tf.setFont(new Font("Baskerville Old Face", 25));
        
        //Grid Pane Layout 
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(15);                                                                                         // Space between rows
        gp.setHgap(10);                                                                                         // Space between columns
        gp.setPadding(new Insets(20));
        gp.add(tf, 0, 1);
        gp.add(press, 0, 2);
        gp.add(label1,0,0);
        gp.add(rules,0,3);
        gp.add(disclaimer, 0, 4);
        gp.setBackground(getbackground());
    

        scene1 = new Scene(gp);                      // Create the scene
        window.setScene(scene1);                              // Set the scene on the stage
        
        window.setMaximized(true); // Launch maximized
        window.setResizable(true); // Let user resize or minimize if they want
        window.show();  

        //Event Handler using Lambda for the "Start Game!" button
        press.setOnAction(e ->{username = tf.getText(); showLevel();});

    }



    /*Displays level selection scene
     * with option buttons and background music
     */
    private void showLevel() 
    {
       
        music("resources/focus.mp3");
        Label chooseLabel = new Label("Choose your Level:");
        chooseLabel.setFont(new Font("Baskerville Old Face", 25));
        chooseLabel.setTextFill(Color.WHITE);

        final Button[] selected = {null};                                                     // One element array to keep track of selected button

        Button button1 = leveloption("Easy", "resources/easyq.txt", selected,"-fx-background-color: green; -fx-border-color: green; -fx-border-width: 2px; -fx-border-radius: 5px;");
        button1.setTextFill(Color.WHITE);
        Button button2 = leveloption("Medium", "resources/mediumq.txt", selected,"-fx-background-color: orange; -fx-border-color: orange; -fx-border-width: 2px; -fx-border-radius: 5px;");
        button2.setTextFill(Color.WHITE);
        Button button3 = leveloption("Hard", "resources/hardq.txt", selected,"-fx-background-color: red; -fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 5px;");
        button3.setTextFill(Color.WHITE);
        
        HBox buttonRow = new HBox(20, button1, button2, button3);
        buttonRow.setAlignment(Pos.CENTER);

        VBox layout = new VBox(30, chooseLabel, buttonRow);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(getbackground());
        layout.setPrefSize(window.getWidth(), window.getHeight());

        Scene levelScene = new Scene(layout);
        window.setScene(levelScene);
}



/**Question Class models the Quiz Question with its text
 * Multiple Choice or True or False
 */

    class Question 
    {
        String questionText;
        List<String> options;
        String correctAnswer;

        public Question(String questionText, List<String> options, String correctAnswer) 
        {
            this.questionText = questionText;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }
    }



/**Loads questions from a file into a list of question objects */

    private List<Question> loadQuestions(String questionfile)                                            //Returns a list containing all the questions from file and question file is the parameter
    {
        List<Question> questions = new ArrayList<>();                                                    //Initialize empty list
        
        try (BufferedReader br = new BufferedReader(new FileReader(questionfile))) 
        {
            String line;
            while ((line = br.readLine()) != null)                                                       //Reads every line until the end which is why theres null
            {
                System.out.println(line);
                String[] parts = line.split(";");                                                  // Split the question and options/answer at the semicolon
                String questionText = parts[0];                                                          // The question itself
                List<String> options = new ArrayList<>();

            for (int i = 1; i < parts.length - 1; i++)                                                  //Loop for options should be everything from index 1 to second-last element (second last element is correct answer)
            {                 
                options.add(parts[i]);
            }

            String correctAnswer = parts[parts.length - 1].trim();                                       // The correct answer is the last part

            questions.add(new Question(questionText, options, correctAnswer));                          //New question object and adds to question list
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Error reading questions file: " + e.getMessage());
        }
        return questions;
    }
   
    

    /**Displays the quiz question scene with options and a submit button.
     * Loads questions one by one and allows the user to pick an option and 
     * submit it.
     */

    private void showScene2()                                                                          //Displays quiz screen after username is typed and start button is clicked
    {
        selectedAnswer = null;
        List<Question> questions = loadQuestions(level);                     //Reads the questions and stores them in a list


        if (currentQuestionIndex >= questions.size()) 
        {
            showscene7();                                                                             //For checking if all questions are done to move on to leaderboard
            return;
        }

        Question currentQuestion = questions.get(currentQuestionIndex);                              //To get the current question the user is on

        Label questionLabel = new Label(currentQuestion.questionText);
        questionLabel.setFont(new Font("Baskerville Old Face", 23));
        questionLabel.setTextFill(Color.WHITE);

        

        VBox optionsBox = new VBox(10);
        optionsBox.setAlignment(Pos.CENTER);                                                        //For all the option buttons to be aligned vertically

        //Stores selected button to reset style when user changes choice
        final Button[] selectedButton = {null}; 

        List<Button> buttons = new ArrayList<>();                                                  // List of button objects (used for feedback label)

        for (String option : currentQuestion.options)                                              //Loop for listing the options for all the questions one by one
        {
            Button optionButton = new Button(option);
            optionButton.setFont(new Font("Baskerville Old Face", 19));
            optionButton.setTextFill(Color.WHITE);
            optionButton.setPrefWidth(400);

            String defaultStyle = "-fx-background-color: goldenrod; -fx-border-color: goldenrod; -fx-border-width: 2px; -fx-border-radius: 5px;";
            String selectedStyle = "-fx-background-color: lightcoral; -fx-border-color: lightcoral; -fx-border-width: 2px; -fx-border-radius: 5px;";
            
            optionButton.setStyle(defaultStyle);

            optionButton.setOnAction(e -> {if (selectedButton[0] != null) selectedButton[0].setStyle(defaultStyle);          // Reset previous button
            optionButton.setStyle(selectedStyle);                                                                            // Highlight selected button
            selectedButton[0] = optionButton;
            selectedAnswer = option;});                                                                                     //Selected answer is updated

            buttons.add(optionButton);                                                                                      // adding options into list
            optionsBox.getChildren().add(optionButton);

            
            
        }

        
    
        Label feedback = new Label("");                                                        //Label that diplays if an answer is right or wrong
        feedback.setFont(new Font("Baskerville Old Face", 16));
        feedback.setTextFill(Color.WHITE);

        Button next = new Button("Next");
        next.setFont(new Font("Baskerville Old Face", 15));
        next.setTextFill(Color.WHITE);
        next.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, new CornerRadii(5), Insets.EMPTY)));
        next.setStyle("-fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 5px;");
        
/**
 * Action handler for the 'Next' button. It checks if an answer is selected,
 * compares it to the correct answer, and updates the score accordingly.
 * A delay is introduced before proceeding to the next question.
 */

        next.setOnAction(e -> {                                                                                
        {  
            if (selectedAnswer != null)                                                               // Checks if an answer is selected
            {
                boolean select = selectedAnswer.equals(currentQuestion.correctAnswer);              // Compares selected answer to the correct one  
                for (Button button : buttons)       
                {
                    button.setDisable(true);                                                         // Disable every button in buttons list
                }
                if (select == true)                                                                 
                {
                    feedback.setText("Correct. Well, well… look who read the question");                          // If answer is correct
                    feedback.setTextFill(Color.GREEN);
                    score++;                                                                       // Increment score
                }
                else
                {
                    feedback.setText("Wrong. Was that… a guess? ");                                   // If asnwer is wrong
                    feedback.setTextFill(Color.RED);
                }             


            PauseTransition pause = new PauseTransition(Duration.seconds(1.0));                      // Creates a delay of 1.0s before moving on to next question
            pause.setOnFinished(event -> {currentQuestionIndex++;showScene2();});                    // After pause, move onto next question
            pause.play();
            }                                                                                         
        
        }
        });

        Button submit = new Button("Submit");
        submit.setFont(new Font("Baskerville Old Face", 15));
        submit.setTextFill(Color.WHITE);
        submit.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, new CornerRadii(5), Insets.EMPTY)));
        submit.setStyle("-fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 5px;");
        submit.setVisible(false); 

         
        submit.setOnAction(e -> {
            if (selectedAnswer != null)                                                               // Checks if an answer is selected
            {
                boolean select = selectedAnswer.equals(currentQuestion.correctAnswer);              // Compares selected answer to the correct one  
                for (Button button : buttons)       
                {
                    button.setDisable(true);                                                         // Disable every button in buttons list
                }
                if (select == true)                                                                 
                {
                    feedback.setText("Correct Answer! Good Job :)");                          // If answer is correct
                    feedback.setTextFill(Color.GREEN);
                    score++;                                                                       // Increment score
                }
                else
                {
                    feedback.setText("Wrong Answer! :(");                                   // If asnwer is wrong
                    feedback.setTextFill(Color.RED);
                }             


            PauseTransition pause = new PauseTransition(Duration.seconds(1.0));                      // Creates a delay of 1.0s before moving on to next question
            pause.setOnFinished(event -> {currentQuestionIndex++;showScene2();});                    // After pause, move onto next question
            pause.play();
            }                                                                                         
        
        });

        if (currentQuestionIndex == questions.size() - 1) {
            submit.setVisible(true);   // Show Submit on last question
            next.setDisable(true);     // Optionally disable Next
        }


        

        VBox quizLayout = new VBox(20, questionLabel, optionsBox, timerLabel,feedback, next,submit);
        quizLayout.setAlignment(Pos.CENTER);
        quizLayout.setBackground(getbackground());
        quizLayout.setPrefSize(window.getWidth(), window.getHeight());

        scene2 = new Scene(quizLayout);
        window.setScene(scene2);
    }

    


    /**Displays the final scene showing the 
     * Score and Leaderboard
     */
        private void showscene7()
        {
            music("resources/audio.mp3");
            int timeTaken = 300 - timeLeft;
            ConnectServer(username, score, timeTaken);                                                    // To connect to server
            Savescore(username, score);

            
            Label resultLabel = new Label("Quiz Completed!\nYour Score: " + score);
            resultLabel.setFont(new Font("Baskerville Old Face", 25));
            resultLabel.setTextFill(Color.WHITE);

            Label leadTitle = new Label("\nLeaderBoard");
            leadTitle.setFont(new Font("Baskerville Old Face", 30));
            leadTitle.setStyle("-fx-background-color: cadetblue; -fx-padding: 3px; -fx-font-size: 25px; -fx-text-fill: white;-fx-font-family: 'Baskerville Old Face'");
            StackPane leaderboardContainer = new StackPane(leadTitle);                                      //Allows stacking of elements in stackpane
            leaderboardContainer.setStyle("-fx-background-color: cadetblue; -fx-padding: 3px;");
            leaderboardContainer.setAlignment(Pos.CENTER);

            GridPane leaderboard = Leaderboard();

            VBox layout = new VBox(20, resultLabel,leaderboardContainer,leaderboard);
            layout.setAlignment(Pos.CENTER);
            layout.setBackground(getbackground());
            layout.setPrefSize(window.getWidth(), window.getHeight());

            scene7 = new Scene(layout);
            window.setScene(scene7);

        }

    


/**Saves the scores of the user in "leaderboard.txt" by appending it to the file */       
private void Savescore(String username, int score)                                                          //Using try-with clause
{ 
    int timetaken = 300 - timeLeft;
    try (FileWriter fWriter = new FileWriter("leaderboard.txt", true))                                           
    { 
        fWriter.write(username + "," + score + ","+ timetaken + "\n");
    } 
    catch (IOException e) 
    {
        System.out.println("Error");
    }
}




/**Reads scores from leaderboard.txt, sorts them in descending order and byt time taken, specifies the formatting of the leaderboard */
    private GridPane Leaderboard() 
    {
        List<String[]> scores = new ArrayList<>();                                                     // Stores scores as list containing array of strings so its easy to split the elements
        try 
        {
            FileReader fReader = new FileReader ("leaderboard.txt");
            BufferedReader bReader = new BufferedReader(fReader); 
            String line;
            while ((line = bReader.readLine()) != null) 
            {
                String[] parts = line.split(",");                                               // Split lines at occurrence of ','
        
                if (parts.length == 3) 
                {
                    scores.add(parts);                                                                // Add to scores list
                }
        }
        }   
        catch (IOException e) 
    {
        System.out.println("Error displaying Leaderboard :((");
    }


//Sort scores in descending order,  and ascending in time taken
    scores.sort((a, b) -> 
    {
        int Ascore = Integer.parseInt(a[1]);                                            // Converts score to integer
        int Bscore = Integer.parseInt(b[1]);
        int Atime = 300 - Integer.parseInt(a[2]);                                      // Converting time to integer and subtracting from total time
        int Btime = 300 - Integer.parseInt(b[2]);

        if (Bscore != Ascore) 
        {
            return Integer.compare(Bscore, Ascore);                                   // If scores are not equal compare the scores (in descedning order)
        }
        return Integer.compare(Atime, Btime);                                        // Otherwise compare time (in asceneding order)
    });

    GridPane leadgp = new GridPane();                                                      //GridPane for leaderboard
    leadgp.setHgap(40);                                                                    // Horizontal Gap
    leadgp.setVgap(10);                                                                    // Vertical Gap
    leadgp.setAlignment(Pos.CENTER);

    String[] headings = {"Username", "Score", "Time Taken"};                                // Array of Strings that stores header
    for (int i = 0; i < headings.length; i++)                                             // Parse through every heading
    {
        Label headers = new Label (headings[i]);
        headers.setFont(new Font("Baskerville Old Face", 25));
        headers.setTextFill(Color.WHITE);
        leadgp.add(headers,i,0);                                                        // positioning of headers
    }
    

    int row = 1;                                                                       // Since row 0 is for headings
    for (String [] display : scores )                                                  // For each element in scores list
    {
        String timedisplay = String.format("%d:%02d", Integer.parseInt(display[2]) / 60, Integer.parseInt(display[2]) % 60);   //Formatting to stre time in M:SS; first converts time intot int and dicvides by 60 for mins and takes the remainder as seconds
    
        Label name = new Label (display[0]);
        name.setFont(new Font("Baskerville Old Face",19));
        name.setTextFill(Color.WHITE);
        leadgp.add(name,0,row);                                                       // row = current row

       
        Label score = new Label (display[1]);
        score.setFont(new Font("Baskerville Old Face",19));
        score.setTextFill(Color.WHITE);
        score.setMaxWidth(Double.MAX_VALUE);
        score.setAlignment(Pos.CENTER);
        leadgp.add(score,1,row);

        
        Label time = new Label (timedisplay);
        time.setFont(new Font("Baskerville Old Face",19));
        time.setTextFill(Color.WHITE);
        time.setMaxWidth(Double.MAX_VALUE);
        time.setAlignment(Pos.CENTER);
        leadgp.add(time,2,row);

        row++;
        
    }


    return leadgp;
}


private void ConnectServer (String username, int score, int timeTaken)
{

/**
 * Connects to a server and sends the username, score, and time taken data. 
 * It also receives a response from the server and displays it in a message dialog.
 */
    try
    {
        Socket socket = new Socket("localhost", 12345);                                         // Creates connection to server
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);                     // Sends data to server through outputstream
        out.println(username);
        out.println(score);
        out.println(timeTaken);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));         // Reads response recived from server
        String line;
        StringBuilder content = new StringBuilder();                                                    // Used to build a single entire whole message
        while ((line = in.readLine()) != null)
        {
            System.out.println(line);
            content.append(line).append("\n");                                                     // Add or append info to stringbuilder
        }

        JOptionPane.showMessageDialog(null, content.toString(), "Mini Leaderboard", JOptionPane.INFORMATION_MESSAGE);    //Pop up message not linked to any windo, has title Mini Leaderboard and displays info icon

        in.close();
        out.close();
        socket.close();

    }
    catch (IOException e)
    {
        e.printStackTrace();
    }
    
}

    public static void main(String[] args) {
        launch(args);}


}