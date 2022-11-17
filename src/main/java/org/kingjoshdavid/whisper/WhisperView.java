package org.kingjoshdavid.whisper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhisperView {

    public static final Pattern WHISPER_PATTERN = Pattern.compile("(.*) - (.*): (\\d+)");
    public static final int INITIAL_SIZE = 600;
    public static final Color COLOR_ONE = new Color(0, 0, 255);
    public static final Color COLOR_TWO = new Color(0, 255, 0, 128);
    public static final Color COLOR_THREE = new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 128);
    public static final Color COLOR_FOUR = new Color(Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), 128);
    private TownSquareWhispersPanel drawingArea;

    public WhisperView(String townSquare, String whisperLog, String descriptor, Map<String, String> namePreferences) {
        String[] names = townSquare.trim().split("\n");
        Map<PlayerPair, Integer> playerPlayerWeights = computeWhisperMap(whisperLog);

        outputGraphSwing(descriptor, names, playerPlayerWeights, namePreferences);
    }

    public WhisperView(String townSquare, String whisperLog, String descriptor) {
        this(townSquare, whisperLog, descriptor, new HashMap<>());
    }

    public static Map<PlayerPair, Integer> computeWhisperMap(String whisperLog) {
        Map<PlayerPair, Integer> playerPlayerWeights = new TreeMap<>();
        for (String whisper : whisperLog.trim().split("\n")) {
            Matcher matcher = WhisperView.WHISPER_PATTERN.matcher(whisper);
            if (!matcher.matches()) {
                System.out.println("Skipping input " + whisper);
                continue;
            }
            String a = matcher.group(1);
            String b = matcher.group(2);
            if (a.compareToIgnoreCase(b) > 0) {
                String c = a;
                a = b;
                b = c;
            }

            int count = Integer.parseInt(matcher.group(3));
//            System.out.println(a + ", " + b + ": " + count);

            PlayerPair pair = new PlayerPair(a, b);
            playerPlayerWeights.merge(pair, count, Integer::sum);
        }
        return playerPlayerWeights;
    }

    private void outputGraphSwing(final String descriptor, final String[] names, final Map<PlayerPair, Integer> playerPlayerWeights, Map<String, String> namePreferences) {
        JPanel viewAndControls = getPanelToDraw(descriptor);
        viewAndControls.setLayout(new BorderLayout());

        drawingArea = new TownSquareWhispersPanel(names, namePreferences);
        drawingArea.addWhispers(playerPlayerWeights, COLOR_ONE);

        viewAndControls.add(drawingArea.controls(), BorderLayout.SOUTH);
        viewAndControls.add(drawingArea, BorderLayout.CENTER);
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
        static class ColoredMap {
            public final Map<PlayerPair, Integer> playerPlayerWeights;
            public final Color color;

            ColoredMap(Map<PlayerPair, Integer> playerPlayerWeights, Color color) {
                this.playerPlayerWeights = playerPlayerWeights;
                this.color = color;
            }
        }

        private final String[] names;
        private final ArrayList<ColoredMap> playerPlayerWeightList;
        private Map<String, String> namePreferences;
        private JSlider playerOffset;
        private JSlider playerSize;
        private double centerX;
        private double centerY;

        public TownSquareWhispersPanel(String[] names, Map<String, String> namePreferences) {
            this.names = names;
            playerPlayerWeightList = new ArrayList<>();
            this.namePreferences = namePreferences;
            this.repaint();
        }

        @Override
        protected void paintComponent(Graphics og) {
            super.paintComponent(og);
            Graphics2D g = (Graphics2D) og;
            g.setPaint(Color.WHITE);
            double max_x = getWidth();
            double max_y = getHeight();

            centerX = max_x / 2.0;
            centerY = max_y / 2.0;
            g.fill(new Rectangle2D.Double(0, 0, max_x, max_y));

            int playerDiameter = playerSize.getValue();
            double playerRadius = playerDiameter / 2.0;

            int playerCount = names.length;
            g.setPaint(Color.BLACK);

            Map<String, Point2D.Double> whisperOrigins = new HashMap<>();
            Map<String, Point2D.Double> playerCenters = new HashMap<>();

            double lineHeight = g.getFontMetrics().getHeight();

            int value = playerOffset.getValue();
            double distToCenter = value / 100.0 * Math.min(centerX, centerY);
            for (int i1 = 0; i1 < names.length; i1++) {
                double angle = i1 * Math.PI * 2.0 / playerCount;

                double cosTheta = Math.cos(angle);
                double sinTheta = Math.sin(angle);
                Point2D.Double playerCenter = new Point2D.Double(centerX + cosTheta * distToCenter, centerY + sinTheta * distToCenter);

                g.draw(new Ellipse2D.Double(playerCenter.x - playerRadius, playerCenter.y - playerRadius, playerDiameter, playerDiameter));

                String displayedName = firstWordOf(names[i1]);
                double nameWidth = stringWidth(g, displayedName);

                double nameDist = distToCenter + playerDiameter + playerRadius / 2.0;
                Point2D.Double nameBoundary = new Point2D.Double(centerX + cosTheta * nameDist, centerY + sinTheta * nameDist);

                //offset the name to be outside the circle
                g.drawString(displayedName, (float) (nameBoundary.x + Math.min(Math.signum(cosTheta) * nameWidth, 0)), (float) (nameBoundary.y + Math.max(Math.signum(sinTheta) * lineHeight, 0)));

                //offset whisper lines by diameter
                Point2D.Double whisperCenter = new Point2D.Double(centerX + cosTheta * (distToCenter - playerRadius), centerY + sinTheta * (distToCenter - playerRadius));
                whisperOrigins.put(names[i1], whisperCenter);
                playerCenters.put(names[i1], playerCenter);
            }

            for (ColoredMap coloredMap : playerPlayerWeightList) {
                g.setPaint(coloredMap.color);
                Map<PlayerPair, Integer> playerPlayerWeights = coloredMap.playerPlayerWeights;
                for (PlayerPair playerPair : playerPlayerWeights.keySet()) {
                    String a = playerPair.playerA;
                    String b = playerPair.playerB;
                    Point2D.Double aCenter = whisperOrigins.get(a);
                    Point2D.Double bCenter = whisperOrigins.get(b);

                    boolean storyTellerMode = false;
                    if (aCenter == null) {
                        System.out.println("Assumed storyteller: " + a);
                        storyTellerMode = true;
                        if (bCenter == null) {
                            System.out.println("What is going on with " + a + " and " + b + "?");
                            continue;
                        }
                        aCenter = bCenter;
                        a = b;
                    } else if (bCenter == null) {
                        System.out.println("Assumed storyteller: " + b);
                        storyTellerMode = true;
                    }
                    Integer tally = playerPlayerWeights.get(playerPair);
                    int modifiedForLog = tally + 1;
                    double thickness = Math.log(modifiedForLog);
//                    g.setStroke(new BasicStroke((float) thickness));
                    double number = tally;
//                    double number = Math.log(modifiedForLog)/Math.log(2);
                    double jitter = playerRadius + 0;
                    if (storyTellerMode) {
                        Point2D.Double pCenter = playerCenters.get(a);
                        g.setPaint(Color.red);
                        String s = "S";
                        int sw = stringWidth(g, s);

                        g.drawString(s, (float) (pCenter.x - sw / 2.0), (float) (pCenter.y + lineHeight * 0.25));

                        g.setPaint(coloredMap.color);
                    } else {
                        for (int i = 0; i < number; i++) {
                            g.draw(new Line2D.Double(roughly(aCenter.x, jitter), roughly(aCenter.y, jitter), roughly(bCenter.x, jitter), roughly(bCenter.y, jitter)));
                        }
                    }
                }
            }

        }

        private int stringWidth(Graphics2D g, String displayedName) {
            return g.getFontMetrics().stringWidth(displayedName);
        }

        private String firstWordOf(String name) {
            String preference = namePreferences.get(name);
            if (preference != null) {
                return preference;
            }
            Pattern fw = Pattern.compile("(\\w*).*");
            Matcher matcher = fw.matcher(name);
            if (matcher.matches())
                return matcher.group(1);
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

            controlHolder.add(playerOffset);
            controlHolder.add(playerSize);
            return controlHolder;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            repaint();
        }

        public void addWhispers(Map<PlayerPair, Integer> whispers, Color color) {
            playerPlayerWeightList.add(new ColoredMap(whispers, color));
        }
    }

    void addWhispers(String whispers, Color color) {
        drawingArea.addWhispers(computeWhisperMap(whispers), color);
    }


    public static class PlayerPair implements Comparable<PlayerPair> {
        public final String playerA;
        public final String playerB;

        PlayerPair(String playerA, String playerB) {
            this.playerA = playerA;
            this.playerB = playerB;
        }

        @Override
        public int compareTo(PlayerPair o) {
            int i = playerA.compareToIgnoreCase(o.playerA);
            if (i == 0) {
                i = playerB.compareTo(o.playerB);
            }
            if (i == 0) {
                i = playerA.compareTo(o.playerA);
            }
            if (i == 0) {
                i = playerB.compareTo(o.playerB);
            }
            return i;
        }

        @Override
        public String toString() {
            return playerA + " - " + playerB;
        }

    }
}
