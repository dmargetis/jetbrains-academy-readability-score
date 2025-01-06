package readability;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        String fileName = args[0];
        Readability readability = new Readability(fileName);
        readability.run();
    }
}

class Readability {
    private final String fileName;

    public Readability(String fileName) {
        this.fileName = fileName;
    }

    public void run() {
        String text = readFromFile();
        System.out.println(text);
        int words = countWords(text);
        int sentences = countSentences(text);
        int characters = countCharacters(text);
        int syllables = countSyllables(text);
        int polysyllables = countPolysyllables(text);

        System.out.println("Words: " + words);
        System.out.println("Sentences: " + sentences);
        System.out.println("Characters: " + characters);
        System.out.println("Syllables: " + syllables);
        System.out.println("Polysyllables: " + polysyllables);


        double scoreARI = getARIScore(words, sentences, characters);
        int ageARI = getAge(scoreARI);
        double scoreFK = getFKScore(words, sentences, syllables);
        int ageFK = getAge(scoreFK);
        double scoreSMOG = getSMOGScore(polysyllables, sentences);
        int ageSMOG = getAge(scoreSMOG);
        double scoreCL = getCLScore(words, sentences, characters);
        int ageCL = getAge(scoreCL);

        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        Scanner sc = new Scanner(System.in);
        String scoreChoice = sc.nextLine();
        switch(scoreChoice) {
            case "ARI" :
                System.out.printf("Automated Readability Index: %.2f (about %d-year-olds)%n", scoreARI, ageARI);
                break;
            case "FK" : System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d-year-olds)%n", scoreFK, ageFK);
                break;
            case "SMOG" :
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d-year-olds)%n", scoreSMOG, ageSMOG);
                break;
            case "CL" :
                System.out.printf("Coleman–Liau index: %.2f (about %d-year-olds)%n", scoreCL, ageCL);
                break;
            case "all" :
                System.out.printf("Automated Readability Index: %.2f (about %d-year-olds)%n", scoreARI, ageARI);
                System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d-year-olds)%n", scoreFK, ageFK);
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d-year-olds)%n", scoreSMOG, ageSMOG);
                System.out.printf("Coleman–Liau index: %.2f (about %d-year-olds)%n", scoreCL, ageCL);
                System.out.printf("%nThis text should be understood in average by %.2f-year-olds.", (ageARI + ageFK + ageSMOG + ageCL) / 4.0);
                break;
            default :
                System.out.println("Not valid choice.");
        }
    }

    public String readFromFile() {
        String pathToFile = fileName;
        File file = new File(pathToFile);
        StringBuilder s = new StringBuilder();
        try(Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                s.append(scanner.nextLine()).append(" ");
            }
        } catch(FileNotFoundException ex) {
            System.out.println("File not found: " + pathToFile);
        }
        return s.toString();
    }

    public int countWords(String text) {
        return text.split("[a-zA-Zα-ωΑ-Ω0-9',]+").length-1;
    }

    public int countSentences(String text) {
        int sentences = text.split("[.!?]").length;
        return (text.trim().charAt(text.trim().length()-1) == '.') ? sentences -1 : sentences;
    }

    public int countCharacters(String text) {
        return text.split("\\S").length-1;
    }

    //Automated Readability Index
    public double getARIScore(int words, int sentences, int characters) {
        return 4.71 * ((double) characters / words) + 0.5 * ((double) words / sentences) - 21.43;
    }

    //Flesch–Kincaid readability tests
    public double getFKScore(int words, int sentences, int syllables) {
        return 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
    }

    public double getSMOGScore(int polysyllables, int sentences) {
        return 1.043 * Math.sqrt((double) polysyllables * ((double) 30 / sentences)) + 3.1291;
    }

    public double getCLScore(int words, int sentences, int characters) {
        double L = ((double) characters / words) * 100.0;
        double S = ((double) sentences / words) * 100.0;
        return 0.0588 * L - 0.296 * S - 15.8;
    }

    public int countSyllables(String text) {
        int count = 0;
        String[] wordsArray = text.split("\\s");
        int syllablesInWord;
        for(String word : wordsArray) {
            syllablesInWord = countVowels(word);
            if(word.length() > 1) {
                char last = word.charAt(word.length() - 1);
                char penultimate = word.charAt(word.length() - 2);
                if (syllablesInWord > 1 && (last == 'e' || (penultimate == 'e' && ((last == '.') || last == '!') || (last == '?')))) {
                    syllablesInWord--;
                }
            }
            else if(syllablesInWord == 0) {
                syllablesInWord++;
            }
            count += syllablesInWord;
        }
        return count;
    }

    public int countVowels(String text) {
        int count = 0;
        String regex = "[aeiouyAEIOUY]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    public int countPolysyllables(String text) {
        int count = 0;
        String[] wordsArray = text.split("\\s+");
        for(String word : wordsArray) {
            if(countSyllables(word) > 2) {
                count++;
            }
        }
        return count;
    }

    public int getAge(double score){
        int age;
        if (score >= 14.0) {
            age = 22;
        }
        else {
            age =(int)Math.ceil(score) + 5;
        }
        return age;
    }
}
