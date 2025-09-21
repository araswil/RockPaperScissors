package ui;

import gameEngine.RockPaperScissors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

import static javax.swing.UIManager.*;

/**
 * Simple Game UI for Rock-Paper-Scissors
 * @Author: Andreas Raschle
 * @Date: September 21 2025
 */
public class RockPaperScissorsUi extends JFrame {

    //Ui Controls
    private final JLabel lblTitle = new JLabel("Rock–Paper–Scissors", SwingConstants.CENTER);
    private final JLabel lblStatus = new JLabel("Choose your move to start!", SwingConstants.CENTER);
    private final JLabel lblScoreHvsC = new JLabel("You 0 : 0 CPU (Draws: 0)", SwingConstants.CENTER);
    private final JLabel lblScoreCvsC = new JLabel("CPU one 0 : 0 CPU two (Draws: 0)", SwingConstants.CENTER);
    private final JLabel lblGameMode = new JLabel("Human versus PC Mode", SwingConstants.CENTER);
    private final JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
    private final JRadioButton optPlayerVsPc = new JRadioButton("Player vs. Pc", true);
    private final JRadioButton optPcVsPc = new JRadioButton("Pc vs. Pc");

    private final JComboBox<Integer> bestOfCombo = new JComboBox<>(new Integer[]{1,3,5,7,9});
    private final JCheckBox chkSpockToggle = new JCheckBox("Lizard–Spock mode");
    private final JButton btnNewMatch = new JButton();
    private final JButton btnRunAutomatic = new JButton();



    private RockPaperScissors rockPaperScissors;

    private final Random rng = new Random();
    private boolean isAutomatRunning = false;

    public RockPaperScissorsUi() {
        super("Andi's Rock–Paper–Scissors with options");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 420));
        setLocationByPlatform(true);

        try {
            setLookAndFeel(getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            //show default look and feeling
        }

        //Get the unerlaining Game instance
        this.rockPaperScissors = RockPaperScissors.getInstance();
        this.createUiContent();
    }

    /**
     * Init the controls and add it to the UI
     */
    private void createUiContent() {
        ImageIcon icon = new ImageIcon("src/resources/rock-paper-scissors.png");
        this.setIconImage(icon.getImage());
        this.setIcon(this.btnNewMatch,"New", "New Match");
        this.setIcon(this.btnRunAutomatic, "Run", "Run Automatic");

        // Top Panel
        JPanel top = new JPanel(new GridLayout(3, 1, 6, 6));
        top.add(lblGameMode);
        top.add(lblTitle);
        top.add(lblStatus);
        top.add(lblScoreHvsC);
        this.lblScoreCvsC.setVisible(false);
        top.add(lblScoreCvsC);

        // Controls row
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        controls.add(new JLabel("Best‑of:"));
        bestOfCombo.setSelectedItem(3);
        bestOfCombo.addActionListener(e -> this.recalcTargetWins());
        controls.add(bestOfCombo);
        controls.add(chkSpockToggle);
        controls.add(optPlayerVsPc);
        controls.add(optPcVsPc);
        controls.add(btnNewMatch);
        controls.add(btnRunAutomatic);

        btnNewMatch.addActionListener(this::initNewMatch);
        btnRunAutomatic.addActionListener(this::initNewMatchAutomat);
        chkSpockToggle.addActionListener(e -> this.resetMoveButtons());
        optPlayerVsPc.setMnemonic(KeyEvent.VK_C);
        optPlayerVsPc.addActionListener(e -> this.togglePlayerMode());
        optPcVsPc.setMnemonic(KeyEvent.VK_P);
        optPcVsPc.addActionListener(e -> this.togglePlayerMode());
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(optPlayerVsPc);
        buttonGroup.add(optPcVsPc);

        // Buttons panel (moves)
        this.resetMoveButtons();

        // Layout
        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        content.setLayout(new BorderLayout(10, 10));
        content.add(top, BorderLayout.NORTH);
        content.add(buttonPanel, BorderLayout.CENTER);
        content.add(controls, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        this.recalcTargetWins();

    }

    /**
     * Play a round
     * @param userMove see RockPaperScissors.Move
     */
    private void playRound(RockPaperScissors.Move userMove) {
        this.rockPaperScissors.playRound(userMove);
        this.lblStatus.setText(this.rockPaperScissors.getStatusText());
        this.lblScoreHvsC.setText(this.rockPaperScissors.getScoreText());
        if (this.rockPaperScissors.getIsGameFinished()){
            JOptionPane.showMessageDialog(this,
                    this.rockPaperScissors.getResultMessage(),
                    "Match Over", JOptionPane.INFORMATION_MESSAGE);
            this.lblStatus.setText(this.rockPaperScissors.getStatusText());
            this.enableMoveButtons(false);
        }
    }

    /**
     * Calculate required wins and set labels
     */
    private void recalcTargetWins() {
        int bestOf = (Integer) bestOfCombo.getSelectedItem();
        this.rockPaperScissors.setTargetWins(bestOf / 2 + 1);
        String title = "Rock–Paper–Scissors";
        if (chkSpockToggle.isSelected()){
            title += " Lizard–Spock";
        }
        title += " — Best‑of " + bestOf + " (first to " + this.rockPaperScissors.getTargetWins() + ")";

        lblTitle.setText(title);
        this.rockPaperScissors.getScoreText();
    }

    /**
     * Enable the buttons to choose from Rock, Scissor, Paper and in extended mode, lizard or Spock
     * @param enabled
     */
    private void enableMoveButtons(boolean enabled) {
        for (Component c : buttonPanel.getComponents()) {
            c.setEnabled(enabled);
        }
    }

    /**
     * Eventhandelr of btnNewMatch
     * @param e
     */
    private void initNewMatch(ActionEvent e) {
        this.rockPaperScissors.resetScore();
        lblScoreHvsC.setText(this.rockPaperScissors.getScoreText());
        this.recalcTargetWins();
        lblStatus.setText("New match! Choose your move.");
        this.enableMoveButtons(true);
    }

    /**
     * Eventhandler of btnRunAutomatic
     * @param e
     */
    private void initNewMatchAutomat(ActionEvent e) {
        this.btnNewMatch.setEnabled(false);
        this.rockPaperScissors.resetScore();
        this.recalcTargetWins();
        lblStatus.setText("Automatic Run PC versus PC");

        int maxRounds = 15;
        int playedRounds = 0;
        do {
           RockPaperScissors.Move cpuMove = this.rockPaperScissors.getMoves().
                   get(rng.nextInt(this.rockPaperScissors.getMoves().size()));
           this.playRound(cpuMove);
           playedRounds++;
           if (playedRounds > maxRounds) {
               JOptionPane.showMessageDialog(this,
                       this.rockPaperScissors.getResultMessage(),
                       "Error while run in automatic mode", JOptionPane.ERROR_MESSAGE);
               break;
           }
        } while (!this.rockPaperScissors.getIsGameFinished());
    }

    /**
     * Reset the buttons panel
     */
    private void resetMoveButtons() {
        buttonPanel.removeAll();
        this.rockPaperScissors.setIsLizardSpockOn(chkSpockToggle.isSelected());
        java.util.List<RockPaperScissors.Move> moves = this.rockPaperScissors.getMoves();;
        buttonPanel.setLayout(new GridLayout(1, Math.max(moves.size(), 3), 10, 10));
        for (RockPaperScissors.Move move : moves) {
            JButton b = new JButton();
            this.setIcon(b, move.label, move.label);
            b.setFont(b.getFont().deriveFont(Font.BOLD, 16f));
            b.addActionListener(ev -> this.playRound(move));
            buttonPanel.add(b);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
        pack();
    }

    /**
     * Handles the Radiobutton options
     */
    private void togglePlayerMode() {
        if (this.optPcVsPc.isSelected()){
            btnNewMatch.setEnabled(false);
            btnRunAutomatic.setEnabled(true);
            lblGameMode.setText("PC versus PC mode");
            this.enableMoveButtons(false);
        }
        else {
            btnNewMatch.setEnabled(true);
            btnRunAutomatic.setEnabled(false);
            lblGameMode.setText("Human versus PC mode");
            this.enableMoveButtons(true);
        }
    }

    /**
     * Just for the Eye, add Icons
     * @param button
     * @param buttonName
     * @param alternateText
     */
    private void setIcon(JButton button, String buttonName, String alternateText){
        try {
            switch (buttonName){
                case "Lizard":
                    button.setIcon(new ImageIcon("src/resources/lizard.png"));
                    break;
                case "Paper":
                    button.setIcon(new ImageIcon("src/resources/paper.png"));
                    break;
                case "Spock":
                    button.setIcon(new ImageIcon("src/resources/spock.png"));
                    break;
                case "Scissors":
                    button.setIcon(new ImageIcon("src/resources/scissors.png"));
                    break;
                case "Rock":
                    button.setIcon(new ImageIcon("src/resources/rock.png"));
                    break;
                case "New":
                    button.setIcon(new ImageIcon("src/resources/new.png"));
                    break;
                case "Run":
                    button.setIcon(new ImageIcon("src/resources/startAutomatic.png"));
                    break;
                default:
                    button.setText(alternateText);
            }

        }
        catch (Exception exc) {
            button.setText(alternateText);
        }
    }


}
