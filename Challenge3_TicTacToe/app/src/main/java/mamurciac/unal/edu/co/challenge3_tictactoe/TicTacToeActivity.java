package mamurciac.unal.edu.co.challenge3_tictactoe;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import androidx.appcompat.app.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class TicTacToeActivity extends AppCompatActivity{
    //It represents the game's internal state
    private TicTacToeGame ticTacToeGame;
    //The buttons make up the board
    private Button boardButtons[];
    //Text displayed as game's information (Turn and winner's game)
    private TextView infoGame, infoNumberHumanWins, infoNumberAndroidWins, infoNumberTies;

    //This variables allow to control the game's statistics (Number of games and wins per player)
    private int numberGame = 1, numberHumanWins = 0, numberAndroidWins = 0, numberTies = 0;

    //Menu options
    static final int dialogDifficultyId = 0, dialogQuitId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        boardButtons = new Button[TicTacToeGame.numberSpots];
        boardButtons[0] = findViewById(R.id.spot_one);
        boardButtons[1] = findViewById(R.id.spot_two);
        boardButtons[2] = findViewById(R.id.spot_three);
        boardButtons[3] = findViewById(R.id.spot_four);
        boardButtons[4] = findViewById(R.id.spot_five);
        boardButtons[5] = findViewById(R.id.spot_six);
        boardButtons[6] = findViewById(R.id.spot_seven);
        boardButtons[7] = findViewById(R.id.spot_eight);
        boardButtons[8] = findViewById(R.id.spot_nine);
        infoGame = findViewById(R.id.game_information);
        infoNumberHumanWins = findViewById(R.id.number_human_wins);
        infoNumberAndroidWins = findViewById(R.id.number_android_wins);
        infoNumberTies = findViewById(R.id.number_ties);
        ticTacToeGame = new TicTacToeGame();
        startNewGame();
    }

    private void startNewGame(){
        ticTacToeGame.clearBoard();

        //It resets all buttons
        for(int spot = 0; spot < boardButtons.length; spot++) {
            boardButtons[spot].setText("");
            boardButtons[spot].setEnabled(true);
            boardButtons[spot].setOnClickListener(new ButtonClickListener(spot));
        }

        //According to the turn, human or computer player goes first
        if(numberGame % 2 == 1){
            infoGame.setText(R.string.first_turn_human);
        }else{
            infoGame.setText(R.string.first_turn_computer);
            int move = ticTacToeGame.getComputerMove();
            ticTacToeGame.setMove(TicTacToeGame.computerPlayer, move);
            boardButtons[move].setEnabled(false);
            boardButtons[move].setText(String.valueOf(TicTacToeGame.computerPlayer));
            boardButtons[move].setTextColor(Color.rgb(200, 0, 0));
            infoGame.setText(R.string.turn_human);
        }
        infoNumberHumanWins.setText("Human Wins: " + numberHumanWins);
        infoNumberAndroidWins.setText("Android Wins: " + numberAndroidWins);
        infoNumberTies.setText("Ties: " + numberTies);
    }

    private void setMove(char player, int location){
        ticTacToeGame.setMove(player, location);
        boardButtons[location].setEnabled(false);
        boardButtons[location].setText(String.valueOf(player));

        if(player == TicTacToeGame.humanPlayer){
            boardButtons[location].setTextColor(Color.rgb(0,200,0));
        }else{
            boardButtons[location].setTextColor(Color.rgb(200,0,0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.new_game:
                numberGame++;
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(dialogDifficultyId);
                return true;
            case R.id.quit:
                showDialog(dialogQuitId);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id){
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id){
            case dialogDifficultyId:
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

    //It handles clicks on the gameboard buttons
    private class ButtonClickListener implements View.OnClickListener{
        int location;
        public ButtonClickListener(int location){
            this.location = location;
        }

        public void onClick(View view){
            if(boardButtons[location].isEnabled()){
                setMove(TicTacToeGame.humanPlayer, location);
                //If there isn't winner yet, then it lets the computer make a move
                int winner = ticTacToeGame.checkForWinner();
                if(winner == TicTacToeGame.gameNotFinished){
                    infoGame.setText(R.string.turn_computer);
                    int move = ticTacToeGame.getComputerMove();
                    setMove(TicTacToeGame.computerPlayer, move);
                    winner = ticTacToeGame.checkForWinner();
                }

                if(winner == TicTacToeGame.gameNotFinished){
                    infoGame.setText(R.string.turn_human);
                }else if(winner == TicTacToeGame.gameTied){
                    infoGame.setText(R.string.result_tie);
                    for(int spot = 0; spot < boardButtons.length; spot++){
                        boardButtons[spot].setEnabled(false);
                    }
                    numberTies++;
                    infoNumberTies.setText("Ties: " + numberTies);
                }else if(winner == TicTacToeGame.gameWithHumanWinner){
                    infoGame.setText(R.string.result_human_wins);
                    for(int spot = 0; spot < boardButtons.length; spot++){
                        boardButtons[spot].setEnabled(false);
                    }
                    numberHumanWins++;
                    infoNumberHumanWins.setText("Human Wins: " + numberHumanWins);
                }else{
                    infoGame.setText(R.string.result_computer_wins);
                    for(int spot = 0; spot < boardButtons.length; spot++){
                        boardButtons[spot].setEnabled(false);
                    }
                    numberAndroidWins++;
                    infoNumberAndroidWins.setText("Android Wins: " + numberAndroidWins);
                }
            }
        }
    }
}