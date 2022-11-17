package org.kingjoshdavid.whisper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Comparator.comparingInt;
import static org.kingjoshdavid.whisper.WhisperViewUnranked.WhisperTallyAdvanced.computeWhisperMapAdvanced;


/**
 * An experiment was done to do ranked whispers without tallies.
 *
 * Lots of assumptions were made about how many whispers could have been at what rank.
 */
public class WhisperViewUnranked {

    public static final Pattern WHISPER_PATTERN = Pattern.compile("(.*) - (.*)");
    public static final int INITIAL_SIZE = 600;

    public WhisperViewUnranked(String townSquare, String whisperLog, String descriptor) {
        String[] names = townSquare.trim().split("\n");
        Map<String, Map<String, Integer>> playerPlayerWeights = computeWhisperMapAdvanced(whisperLog);

//        outputGraph(descriptor, names, playerPlayerWeights);
        outputGraphSwing(descriptor, names, playerPlayerWeights);
    }

    private void outputGraphSwing(final String descriptor, final String[] names, final Map<String, Map<String, Integer>> playerPlayerWeights) {
        JPanel viewAndControls = getPanelToDraw(descriptor);
        viewAndControls.setLayout(new BorderLayout());

        TownSquareWhispersPanel drawingArea = new TownSquareWhispersPanel(names, playerPlayerWeights);
        viewAndControls.add(drawingArea, BorderLayout.CENTER);
        viewAndControls.add(drawingArea.controls(), BorderLayout.SOUTH);
    }

    private JPanel getPanelToDraw(String descriptor) {
        JFrame frame = new JFrame(descriptor);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(INITIAL_SIZE, INITIAL_SIZE));
        frame.setVisible(true);

        JPanel viewAndControls = new JPanel();
        frame.setContentPane(viewAndControls);
        return viewAndControls;
    }

    private static class TownSquareWhispersPanel extends JPanel implements ChangeListener {
        private final String[] names;
        private final Map<String, Map<String, Integer>> playerPlayerWeights;
        private JSlider playerOffset;
        private JSlider playerSize;
        private JSlider lowCountRatio;

        public TownSquareWhispersPanel(String[] names, Map<String, Map<String, Integer>> playerPlayerWeights) {
            this.names = names;
            this.playerPlayerWeights = playerPlayerWeights;
        }

        @Override
        protected void paintComponent(Graphics og) {
            super.paintComponent(og);
            Graphics2D g = (Graphics2D) og;
            g.setPaint(Color.WHITE);
            double max_x = getWidth();
            double max_y = getHeight();

            double centerX = max_x / 2.0;
            double centerY = max_y / 2.0;
            g.fill(new Rectangle2D.Double(0, 0, max_x, max_y));

            int value = playerOffset.getValue();
            int playerDiameter = playerSize.getValue();
            double ratioLow = lowCountRatio.getValue() / 100.0;

            double distToCenter = value / 100.0 * Math.min(centerX, centerY);
            double playerRadius = playerDiameter / 2.0;
//            System.out.println(value + ", " + playerDiameter);

            int playerCount = names.length;
            g.setPaint(Color.BLACK);

            Map<String, Point2D.Double> whisperOrigins = new HashMap<>();
            Map<String, Point2D.Double> playerCenters = new HashMap<>();

            double lineHeight = g.getFontMetrics().getHeight();

            for (int i = 0; i < names.length; i++) {
                double angle = i * Math.PI * 2.0 / playerCount;

                double cosTheta = Math.cos(angle);
                double sinTheta = Math.sin(angle);
                Point2D.Double playerCenter = new Point2D.Double(centerX + cosTheta * distToCenter, centerY + sinTheta * distToCenter);

                g.draw(new Ellipse2D.Double(playerCenter.x - playerRadius, playerCenter.y - playerRadius, playerDiameter, playerDiameter));

                String displayedName = firstWordOf(names[i]);
                double nameWidth = stringWidth(g, displayedName);

                double nameDistance = distToCenter + playerRadius;
                Point2D.Double nameBoundary = new Point2D.Double(centerX + cosTheta * nameDistance, centerY + sinTheta * nameDistance);

                //offset the name to be outside the circle
                float xName = (float) (nameBoundary.x + Math.min(Math.signum(cosTheta) * nameWidth, 0));
                float yName = (float) (nameBoundary.y + Math.min(Math.signum(sinTheta) * lineHeight, 0) + lineHeight);
                g.drawString(displayedName, xName, yName);

                //offset whisper lines by diameter
                Point2D.Double whisperCenter = new Point2D.Double(centerX + cosTheta * (distToCenter - playerRadius), centerY + sinTheta * (distToCenter - playerRadius));
                whisperOrigins.put(names[i], whisperCenter);
                playerCenters.put(names[i], playerCenter);
            }

            TreeSet<TallyLine> whispers = new TreeSet<>(comparingInt(o -> o.tally));
            for (String a : playerPlayerWeights.keySet()) {
                Map<String, Integer> pWeights = playerPlayerWeights.get(a);
                for (String b : pWeights.keySet()) {
                    Integer count = pWeights.get(b);
                    whispers.add(new TallyLine(a, b, count));
                }
            }

            Color low = Color.GRAY;
            Color high = Color.BLUE;

            g.setPaint(Color.BLUE);
            double[] doubles = randomLinearPivot(whispers.size(), ratioLow);
            int c = 0;
            for (TallyLine whisper : whispers) {
                g.setStroke(new BasicStroke(4f));
                Point2D.Double aCenter = whisperOrigins.get(whisper.playerA);
                Point2D.Double bCenter = whisperOrigins.get(whisper.playerB);
                boolean storyTellerMode = false;
                if (aCenter == null) {
                    storyTellerMode = true;
                    System.out.println("assumed storyteller: " + whisper.playerA);
                    if (bCenter == null) {
                        System.out.println("What is going on with " + whisper.playerA + " and " + whisper.playerB + "?");
                        continue;
                    }
                    aCenter = bCenter;
                } else if (bCenter == null) {
                    storyTellerMode = true;
                    System.out.println("assumed storyteller: " + whisper.playerB);
                }

                double interpolation = doubles[c];
                g.setPaint(interpolateColor(low, high, interpolation));

                double jitter = playerRadius / 2;
                if (storyTellerMode) {
                    Point2D.Double pCenter = playerCenters.get(whisper.playerA);
                    if (pCenter == null) {
                        pCenter = playerCenters.get(whisper.playerB);
                    }
                    g.setPaint(Color.red);
                    String s = "S";
                    int sw = stringWidth(g, s);

                    g.drawString(s, (float) (pCenter.x - sw / 2.0), (float) (pCenter.y + lineHeight * 0.25));

                    g.setPaint(Color.blue);
                } else {
                    g.draw(new Line2D.Double(roughly(aCenter.x, jitter), roughly(aCenter.y, jitter), roughly(bCenter.x, jitter), roughly(bCenter.y, jitter)));
                }

                c++;
            }
        }

        private Color interpolateColor(Color low, Color high, double interpolation) {
            int red = (int) (interpolation * (high.getRed() - low.getRed()) + low.getRed());
            int green = (int) (interpolation * (high.getGreen() - low.getGreen()) + low.getGreen());
            int blue = (int) (interpolation * (high.getBlue() - low.getBlue()) + low.getBlue());
            int alpha = (int) (Math.max(0.3, interpolation) * 255);
            return new Color(red, green, blue, alpha);
        }

        private static double[] randomLinearPivot(int length, double ratioLow) {
            int numSmallest = Math.max(1, (int) (length * ratioLow));
            double smallest = 0.0;
            double largest = 1.0;
            double rangeSize = largest - smallest;
            double[] toR = new double[length];
            for (int i = 0; i < length; i++) {
                if (i < numSmallest) {
                    toR[i] = smallest;
                } else {
                    toR[i] = Math.random() * rangeSize + smallest;
                }
            }
            Arrays.sort(toR);

            return toR;
        }

        private int stringWidth(Graphics2D g, String displayedName) {
            return g.getFontMetrics().stringWidth(displayedName);
        }

        private String firstWordOf(String name) {
            Pattern fw = Pattern.compile("([^\\p{Punct}]*).*");
            Matcher matcher = fw.matcher(name);
            if (matcher.matches())
                return matcher.group(1).trim();
            return name;
        }

        private double roughly(double value, double jitter) {
            return value + Math.random() * jitter - jitter / 2.0;
        }

        public Component controls() {
            JPanel controlHolder = new JPanel();
            controlHolder.setLayout(new FlowLayout());
            playerOffset = new JSlider(JSlider.HORIZONTAL);
            playerOffset.addChangeListener(this);

            playerSize = new JSlider(JSlider.HORIZONTAL);
            playerSize.setValue(20);
            playerSize.addChangeListener(this);

            lowCountRatio = new JSlider(JSlider.HORIZONTAL);
            lowCountRatio.setValue(10);
            lowCountRatio.addChangeListener(this);

//            controlHolder.add(playerOffset);
//            controlHolder.add(playerSize);
            controlHolder.add(lowCountRatio);
            return controlHolder;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            repaint();
        }
    }

    public static class WhisperTallyAdvanced {

        public static Map<String, Map<String, Integer>> computeWhisperMapAdvanced(String whisperLog) {
            Map<String, Map<String, Integer>> playerPlayerWeights = new HashMap<>();
            String[] whisperLines = whisperLog.trim().split("\n");
            int lc = 0;
            int[] fakeTallies = reverseSeq(whisperLines.length);
            for (String whisper : whisperLines) {
                Matcher matcher = WHISPER_PATTERN.matcher(whisper);
                if (!matcher.matches()) {
                    System.out.println("Skipping input " + whisper);
                    continue;
                }
                String a = matcher.group(1);
                String b = matcher.group(2);
                if (a.compareTo(b) > 0) {
                    String c = a;
                    a = b;
                    b = c;
                }

                int count = 1;
                if (lc < fakeTallies.length) {
                    count = fakeTallies[lc];
                } else {
                    System.out.println("using 1");
                }
                lc++;
                System.out.println(a + ", " + b + ": " + count);

                Map<String, Integer> pw = playerPlayerWeights.computeIfAbsent(a, k -> new HashMap<>());
                if (pw.containsKey(b)) {
                    pw.put(b, pw.get(b) + count);
                } else {
                    pw.put(b, count);
                }
            }
            return playerPlayerWeights;
        }

        private static int[] reverseSeq(int length) {
            int[] s = new int[length];
            for (int i = 0; i < length; i++) {
                s[i] = length - i;
            }
            return s;
        }
    }

    static class TallyLine {
        final String playerA;
        final String playerB;
        final int tally;

        TallyLine(String playerA, String playerB, int tally) {
            this.playerA = playerA;
            this.playerB = playerB;
            this.tally = tally;
        }
    }

    public static void main(String[] args) {
        String townSquare = "" +
                "Albuquerque\n" +
                "Berlin\n" +
                "Chicago\n" +
                "Elm\n" +
                "Frankfurt\n" +
                "Gainsville\n" +
                "Hudson\n" +
                "Indianapolis\n" +
                "Johnson\n" +
                "Knapp\n" +
                "Loveland\n" +
                "Madrid\n" +
                "Nice\n" +
                "Omar\n";

        String dayOne = "Knapp - Loveland\n" +
                "Hudson - Loveland\n" +
                "Chicago - Loveland\n" +
                "Johnson - Knapp\n" +
                "Elm - Madrid\n" +
                "Hudson - Knapp\n" +
                "Johnson - Loveland\n" +
                "Hudson - Johnson\n" +
                "Hudson - Nice\n" +
                "Gainsville - Madrid\n" +
                "Elm - Loveland\n" +
                "Gainsville - Hudson\n" +
                "Madrid - Nice\n" +
                "Chicago - Elm\n" +
                "Chicago - Knapp\n" +
                "Elm - Knapp\n" +
                "Hudson - Madrid\n" +
                "Berlin - Hudson\n" +
                "Gainsville - Knapp\n" +
                "Knapp - Madrid\n" +
                "Berlin - Loveland\n" +
                "Johnson - Madrid\n" +
                "Elm - Hudson\n" +
                "Berlin - Elm\n" +
                "Chicago - Nice\n" +
                "Elm - Johnson\n" +
                "Frankfurt - Knapp\n" +
                "Gainsville - Omar\n" +
                "Madrid - Omar\n" +
                "Nice - Omar\n" +
                "Berlin - Chicago\n" +
                "Berlin - Knapp\n" +
                "Chicago - Hudson\n" +
                "Elm - Frankfurt\n" +
                "Elm - Gainsville\n" +
                "Elm - Nice\n" +
                "Frankfurt - Gainsville\n" +
                "Frankfurt - Hudson\n" +
                "Frankfurt - Nice\n" +
                "Loveland - Omar\n" +
                "Albuquerque - Omar\n" +
                "Elm - Omar\n" +
                "Loveland - Madrid\n" +
                "Albuquerque - Berlin\n" +
                "Albuquerque - Elm\n" +
                "Albuquerque - Loveland\n" +
                "Elm - Indianapolis\n" +
                "Hudson - Indianapolis\n" +
                "Indianapolis - Knapp\n" +
                "Loveland - Nice\n";

        String dayOneTwo = "Albuquerque - Elm\n" +
                "Berlin - Hudson\n" +
                "Albuquerque - Berlin\n" +
                "Albuquerque - Loveland\n" +
                "Albuquerque - Hudson\n" +
                "Elm - Johnson\n" +
                "Albuquerque - Madrid\n" +
                "Berlin - Madrid\n" +
                "Hudson - Loveland\n" +
                "Albuquerque - Johnson\n" +
                "Albuquerque - Chicago\n" +
                "Albuquerque - Frankfurt\n" +
                "Albuquerque - Gainsville\n" +
                "Albuquerque - Indianapolis\n" +
                "Albuquerque - Knapp\n" +
                "Albuquerque - Nice\n" +
                "Albuquerque - Omar\n" +
                "Berlin - Elm\n" +
                "Gainsville - Loveland\n" +
                "Knapp - Loveland\n";

        String dayTwo = "Berlin - Knapp\n" +
                "Hudson - Knapp\n" +
                "Knapp - Loveland\n" +
                "Johnson - Knapp\n" +
                "Berlin - Hudson\n" +
                "Berlin - Loveland\n" +
                "Berlin - Nice\n" +
                "Hudson - Johnson\n" +
                "Berlin - Johnson\n" +
                "Berlin - Gainsville\n" +
                "Albuquerque - Berlin\n" +
                "Albuquerque - Johnson\n" +
                "Hudson - Omar\n" +
                "Albuquerque - Elm\n" +
                "Albuquerque - Knapp\n" +
                "Loveland - Madrid\n" +
                "Albuquerque - Nice\n" +
                "Chicago - Nice\n" +
                "Berlin - Elm\n" +
                "Berlin - Madrid\n" +
                "Elm - Knapp\n" +
                "Albuquerque - Loveland\n" +
                "Nice - Omar\n" +
                "Chicago - Hudson\n" +
                "Johnson - Loveland\n" +
                "Madrid - Nice\n" +
                "Albuquerque - Hudson\n" +
                "Chicago - Knapp\n" +
                "Gainsville - Hudson\n" +
                "Hudson - Indianapolis\n" +
                "Albuquerque - Omar\n" +
                "Elm - Hudson\n" +
                "Elm - Nice\n" +
                "Knapp - Madrid\n" +
                "Knapp - Nice\n" +
                "Hudson - Loveland\n" +
                "Indianapolis - Loveland\n" +
                "Johnson - Omar\n" +
                "Berlin - Chicago\n" +
                "Elm - Johnson\n" +
                "Hudson - Madrid\n" +
                "Hudson - Nice\n" +
                "Indianapolis - Knapp\n" +
                "Berlin - Omar\n" +
                "Chicago - Loveland\n" +
                "Elm - Gainsville\n" +
                "Gainsville - Omar\n" +
                "Albuquerque - Indianapolis\n" +
                "Gainsville - Knapp\n" +
                "Johnson - Madrid\n" +
                "Knapp - Omar\n" +
                "Madrid - Omar\n" +
                "Johnson - Philadelphia\n" +
                "Albuquerque - Chicago\n" +
                "Berlin - Indianapolis\n" +
                "Chicago - Indianapolis\n" +
                "Chicago - Johnson\n" +
                "Elm - Loveland\n" +
                "Frankfurt - Knapp\n" +
                "Frankfurt - Loveland\n" +
                "Frankfurt - Nice\n" +
                "Gainsville - Madrid\n" +
                "Indianapolis - Nice\n" +
                "Loveland - Nice\n";

        String dayThree = "Knapp - Nice\n" +
                "Johnson - Madrid\n" +
                "Hudson - Loveland\n" +
                "Knapp - Madrid\n" +
                "Johnson - Knapp\n" +
                "Hudson - Knapp\n" +
                "Hudson - Johnson\n" +
                "Loveland - Madrid\n" +
                "Chicago - Nice\n" +
                "Chicago - Knapp\n" +
                "Berlin - Madrid\n" +
                "Berlin - Gainsville\n" +
                "Johnson - Loveland\n" +
                "Madrid - Nice\n" +
                "Hudson - Madrid\n" +
                "Frankfurt - Nice\n" +
                "Gainsville - Omar\n" +
                "Gainsville - Nice\n" +
                "Knapp - Loveland\n" +
                "Albuquerque - Loveland\n" +
                "Albuquerque - Nice\n" +
                "Chicago - Madrid\n" +
                "Frankfurt - Hudson\n" +
                "Berlin - Frankfurt\n" +
                "Gainsville - Madrid\n" +
                "Hudson - Indianapolis\n" +
                "Johnson - Philadelphia\n" +
                "Chicago - Johnson\n" +
                "Elm - Frankfurt\n" +
                "Elm - Hudson\n";

        String dayFour = "Berlin - Knapp\n" +
                "Gainsville - Hudson\n" +
                "Elm - Gainsville\n" +
                "Knapp - Madrid\n" +
                "Gainsville - Loveland\n" +
                "Chicago - Nice\n" +
                "Elm - Knapp\n" +
                "Hudson - Loveland\n" +
                "Berlin - Loveland\n" +
                "Knapp - Nice\n" +
                "Gainsville - Johnson\n" +
                "Elm - Johnson\n" +
                "Hudson - Johnson\n" +
                "Berlin - Johnson\n" +
                "Chicago - Gainsville\n" +
                "Berlin - Hudson\n" +
                "Johnson - Loveland\n" +
                "Berlin - Elm\n" +
                "Johnson - Knapp\n" +
                "Berlin - Madrid\n" +
                "Chicago - Johnson\n" +
                "Albuquerque - Berlin\n" +
                "Albuquerque - Gainsville\n" +
                "Chicago - Knapp\n" +
                "Elm - Loveland\n" +
                "Elm - Madrid\n" +
                "Knapp - Loveland\n" +
                "Albuquerque - Chicago\n" +
                "Albuquerque - Hudson\n" +
                "Albuquerque - Knapp\n" +
                "Albuquerque - Loveland\n" +
                "Berlin - Nice\n" +
                "Chicago - Omar\n" +
                "Gainsville - Nice\n" +
                "Hudson - Indianapolis\n" +
                "Hudson - Knapp\n" +
                "Madrid - Nice\n";
        String dayFive = "Chicago - Johnson\n" +
                "Chicago - Nice\n" +
                "Elm - Johnson\n" +
                "Chicago - Elm\n" +
                "Berlin - Elm\n" +
                "Indianapolis - Nice\n" +
                "Albuquerque - Chicago\n" +
                "Albuquerque - Elm\n" +
                "Albuquerque - Johnson\n" +
                "Knapp - Madrid\n";

        new WhisperViewUnranked(townSquare, dayOne, "dayOne");
        new WhisperViewUnranked(townSquare, dayOneTwo, "dayOneTwo");
        new WhisperViewUnranked(townSquare, dayTwo, "dayTwo");
        new WhisperViewUnranked(townSquare, dayThree, "dayThree");
        new WhisperViewUnranked(townSquare, dayFour, "dayFour");
        new WhisperViewUnranked(townSquare, dayFive, "dayFive");

    }

}

