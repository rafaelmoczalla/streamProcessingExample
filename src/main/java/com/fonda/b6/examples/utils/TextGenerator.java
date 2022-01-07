package com.fonda.b6.examples.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TextGenerator {

    public String art[] = { "the", "a", "one", "two", "three", "some", "any" };
    public String noun[] = { "boy", "dog", "car", "bicycle", "player", "game" };
    public String verb[] = { "ran", "jumped", "sang", "moved", "rolled" };
    public String adj[] = { "good", "bad", "funny", "easy", "difficult"};
    public String prep[] = { "away", "towards", "around", "near" };

    public String alph = "abcdefghijklmnopqrstuvwxyz";

    public String playerActivities[] = {
        "login",
        "online",
        "logout"
    };

    public String shopActivities[] = {
        "new",
        "price change",
        "purchase",
        "remove"
    };

    public TextGenerator() {}

    public static void main(String[] args) {
        TextGenerator gen = new TextGenerator();

        String sent = gen.randomParagraph(300);
        System.out.println("len=" + sent.length());
        System.out.println("set=" + sent);

        System.out.println("name=" + gen.randomName(3303));
        System.out.println("loc=" + gen.randomName(3303));
        System.out.println("name=" + gen.randomName(3304));
        System.out.println("loc=" + gen.randomName(0));
    }

    public String numberToString(int id) {
        String out = "";
        int i = id;
        int mod = alph.length();
        int rest = id % mod;

        if (i == 0)
            return out + Character.toUpperCase(alph.charAt(0));

        while (i != 0) {
            out = Character.toUpperCase(alph.charAt(rest)) + out;
            i -= rest;
            i /= mod;
            rest = i % mod;
        }

        return out;
    }

    public String randomName(int id) {
        return "Name" + numberToString(id);
    }

    public String randomLocation(int id) {
        return "Location" + numberToString(id);
    }

    public String randomPlayerActivity(int weight) {
        int rnd = ThreadLocalRandom.current().nextInt(weight + 1);

        if (rnd == 0)
            return playerActivities[0];
        else if (rnd < weight)
            return playerActivities[1];

        return playerActivities[playerActivities.length - 1];
    }

    public String randomShopActivity(int weight) {
        int rnd = ThreadLocalRandom.current().nextInt(weight + 1);

        if (rnd == 0)
            return shopActivities[0];
        else if (rnd == weight)
            return shopActivities[shopActivities.length - 1];

        rnd = ThreadLocalRandom.current().nextInt(shopActivities.length - 2);

        return shopActivities[1 + rnd];
    }

    public String randomItemActivity() {
        return shopActivities[ThreadLocalRandom.current().nextInt(shopActivities.length)];
    }

    public String randomSentence() {
        String sent = "";

        int iArt = ThreadLocalRandom.current().nextInt(art.length);
        int iAdj = ThreadLocalRandom.current().nextInt(adj.length);
        int iNoun = ThreadLocalRandom.current().nextInt(noun.length);
        int iVerb = ThreadLocalRandom.current().nextInt(verb.length);
        int iPrep = ThreadLocalRandom.current().nextInt(prep.length);
        int jArt = ThreadLocalRandom.current().nextInt(art.length);
        int jAdj = ThreadLocalRandom.current().nextInt(adj.length);
        int jNoun = ThreadLocalRandom.current().nextInt(noun.length);

        sent += art[iArt] + " ";
        sent += adj[iAdj] + " ";
        sent += noun[iNoun] + " ";
        sent += verb[iVerb] + " ";
        sent += prep[iPrep] + " ";
        sent += art[jArt] + " ";
        sent += adj[jAdj] + " ";
        sent += noun[jNoun] + ".";

        return Character.toUpperCase(sent.charAt(0)) + sent.substring(1);
    }

    public String randomParagraph(long length) {
        if (length < 63) {
            return "Length has to be at least 54.";
        }

        String sent = "";
        String para = "";
        boolean start = true;

        while ((para + " " + sent).length() < length)
        {
            if (para.isEmpty()) {
                para += sent;
            } else {
                para += " " + sent;
            }

            sent = randomSentence();

            if (start) {
                sent = Character.toUpperCase(sent.charAt(0)) + sent.substring(1);
                start = false;
            }
        }

        return para;
    }

    @Override
    public String toString() {
        String arts = "";
        String nouns = "";
        String verbs = "";
        String adjs = "";
        String preps = "";

        boolean start = true;

        for (String item : art) {
            if (start)
                arts += item;
            else
                arts += ", " + item;
            start = false;
        }

        start = true;

        for (String item : noun) {
            if (start)
                nouns += item;
            else
                nouns += ", " + item;
            start = false;
        }

        start = true;

        for (String item : verb) {
            if (start)
                verbs += item;
            else
                verbs += ", " + item;
            start = false;
        }

        start = true;

        for (String item : adj) {
            if (start)
                adjs += item;
            else
                adjs += ", " + item;
            start = false;
        }

        start = true;

        for (String item : prep) {
            if (start)
                preps += item;
            else
                preps += ", " + item;
            start = false;
        }

        return "SentenceGenerator{" +
                "art=[" + arts +
                "], noun=[" + nouns +
                "], verb=[" + verbs +
                "], adj=[" + adjs +
                "], prep=[" + preps +
                "], alph=" + alph +
                "}";
    }
}