package mamurciac.unal.edu.co.challenge4_tictactoe;

import android.app.Dialog;
import android.content.*;
import androidx.appcompat.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.lang.reflect.*;

public class TicTacToeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    //It represents the game's internal state
    private TicTacToeGame ticTacToeGame;
    private BoardView gameBoardView;

    //Text displayed as game's information (Turn and winner's game)
    private TextView infoGame, infoNumberHumanWins, infoNumberAndroidWins, infoNumberTies;

    //This variables allow to control the game's statistics (Number of games and wins per player)
    private char playerTurn;
    private int numberGame = 1, numberHumanWins = 0, numberAndroidWins = 0, numberTies = 0;
    private boolean gameOver;

    //Menu options
    static final int dialogDifficultySuccessId = 0, dialogDifficultyFailureId = 1, dialogAboutGameId = 2, dialogQuitId = 3;
    private static String popupConstant = "mPopup", popupForceShowIcon = "setForceShowIcon";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        infoGame = findViewById(R.id.game_information);
        infoNumberHumanWins = findViewById(R.id.number_human_wins);
        infoNumberAndroidWins = findViewById(R.id.number_android_wins);
        infoNumberTies = findViewById(R.id.number_ties);
        ticTacToeGame = new TicTacToeGame();

        gameOver = false;
        gameBoardView = findViewById(R.id.game_board);
        gameBoardView.setGame(ticTacToeGame);
        //It listens for touches on the board
        gameBoardView.setOnTouchListener(touchListener);
        startNewGame();
    }

    private void startNewGame(){
        ticTacToeGame.clearBoard();
        gameBoardView.invalidate(); //It redraws the board
        gameOver = false;

        //According to the turn, human or computer player goes first
        if(numberGame % 2 == 1){
            infoGame.setText(R.string.first_turn_human);
        }else{
            playerTurn = TicTacToeGame.computerPlayer;
            infoGame.setText(R.string.first_turn_computer);
            int move = ticTacToeGame.getComputerMove();
            ticTacToeGame.setMove(TicTacToeGame.computerPlayer, move);
            infoGame.setText(R.string.turn_human);
        }
        playerTurn = TicTacToeGame.humanPlayer;
        infoNumberHumanWins.setText("Human Wins: " + numberHumanWins);
        infoNumberAndroidWins.setText("Android Wins: " + numberAndroidWins);
        infoNumberTies.setText("Ties: " + numberTies);
    }

    private boolean setMove(char player, int location){
        if(ticTacToeGame.setMove(player, location) == true){
            gameBoardView.invalidate(); //It redraws the board
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id){
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id){
            case dialogDifficultySuccessId:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {getResources().getString(R.string.difficulty_easy), getResources().getString(R.string.difficulty_medium), getResources().getString(R.string.difficulty_hard)};
                builder.setSingleChoiceItems(levels, ticTacToeGame.getDifficultyLevel().ordinal(), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        dialog.dismiss();
                        ticTacToeGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.valueOf(String.valueOf(levels[item])));
                        Toast.makeText(getApplicationContext(),"Game's Difficulty: " + levels[item], Toast.LENGTH_SHORT).show();
                    }
                });
                dialog = builder.create();
                break;
            case dialogDifficultyFailureId:
                builder.setMessage(R.string.difficulty_not_changeable).setCancelable(true).setPositiveButton(R.string.ok,null);
                dialog = builder.create();
                break;
            case dialogAboutGameId:
                dialog = new Dialog(TicTacToeActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.about_dialog);
                Button okButton = dialog.findViewById(R.id.ok_button);
                okButton.setEnabled(true);
                final Dialog finalDialog = dialog;
                okButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        finalDialog.cancel();
                    }
                });
                break;

            case dialogQuitId:
                builder.setMessage(R.string.quit_question).setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        TicTacToeActivity.this.finish();
                    }
                }).setNegativeButton(R.string.no,null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item){
        switch(item.getItemId()){
            case R.id.new_game:
                numberGame++;
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                if(ticTacToeGame.checkWhetherHumanPlayed() == false){
                    showDialog(dialogDifficultySuccessId);
                }else{
                    showDialog(dialogDifficultyFailureId);
                }
                return true;
            case R.id.about:
                showDialog(dialogAboutGameId);
                return true;
            case R.id.quit:
                showDialog(dialogQuitId);
                return true;
        }
        return false;
    }

    public boolean onMenuItemSelect(MenuItem item){
        showPopup(findViewById(item.getItemId()));
        return true;
    }

    public void showPopup(View view){
        PopupMenu popup = new PopupMenu(TicTacToeActivity.this, view);
        try{
            //It forces to show the menu items' icons
            Field[] fields = popup.getClass().getDeclaredFields();
            for(Field field : fields){
                if(field.getName().equals(popupConstant)){
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(popupForceShowIcon, boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }

        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    //It listens for touches on the board
    private View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent event){
            if(playerTurn == TicTacToeGame.humanPlayer){
                //It determines which cell was touched
                int column = (int) event.getX() / gameBoardView.getBoardCellWidth();
                int row = (int) event.getY() / gameBoardView.getBoardCellHeight();
                int spot = row * 3 + column;

                if(gameOver == false && setMove(TicTacToeGame.humanPlayer, spot) == true){
                    //If there isn't winner yet, then it lets the computer make a move
                    int winner = ticTacToeGame.checkForWinner();
                    if(winner == TicTacToeGame.gameNotFinished){
                        infoGame.setText(R.string.turn_computer);
                        playerTurn = TicTacToeGame.computerPlayer;

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable(){
                            @Override
                            public void run(){
                                int move = ticTacToeGame.getComputerMove();
                                setMove(TicTacToeGame.computerPlayer, move);
                                int winner = ticTacToeGame.checkForWinner();

                                if(winner == TicTacToeGame.gameNotFinished){
                                    infoGame.setText(R.string.turn_human);
                                    playerTurn = TicTacToeGame.humanPlayer;
                                }else if(winner == TicTacToeGame.gameTied){
                                    infoGame.setText(R.string.result_tie);
                                    gameOver = true;
                                    numberTies++;
                                    infoNumberTies.setText("Ties: " + numberTies);
                                }else if(winner == TicTacToeGame.gameWithHumanWinner){
                                    infoGame.setText(R.string.result_human_wins);
                                    gameOver = true;
                                    numberHumanWins++;
                                    infoNumberHumanWins.setText("Human Wins: " + numberHumanWins);
                                }else{
                                    infoGame.setText(R.string.result_computer_wins);
                                    gameOver = true;
                                    numberAndroidWins++;
                                    infoNumberAndroidWins.setText("Android Wins: " + numberAndroidWins);
                                }
                            }
                        }, 1750);
                    }else if(winner == TicTacToeGame.gameTied){
                        infoGame.setText(R.string.result_tie);
                        gameOver = true;
                        numberTies++;
                        infoNumberTies.setText("Ties: " + numberTies);
                    }else if(winner == TicTacToeGame.gameWithHumanWinner){
                        infoGame.setText(R.string.result_human_wins);
                        gameOver = true;
                        numberHumanWins++;
                        infoNumberHumanWins.setText("Human Wins: " + numberHumanWins);
                    }else{
                        infoGame.setText(R.string.result_computer_wins);
                        gameOver = true;
                        numberAndroidWins++;
                        infoNumberAndroidWins.setText("Android Wins: " + numberAndroidWins);
                    }
                    return true;
                }
            }
            //So we aren't notified of continued events when finger is moved
            return false;
        }
    };
}