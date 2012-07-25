package edu.stanford.pcl.news.dataHandlers;

import au.com.bytecode.opencsv.CSVWriter;
import com.martiansoftware.jsap.JSAPResult;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Sampler {
    LinkedHashMap<String, ArrayList> results = new LinkedHashMap<String, ArrayList>();

    public void saveFile(String outputFile) throws IOException {
        Iterator<Map.Entry<String, ArrayList>> iterator = results.entrySet().iterator();
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile), '\t');

        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            ArrayList<String> row = (ArrayList) entry.getValue();
            String[] fullRow = row.toArray(new String[row.size()]);
            writer.writeNext(fullRow);
        }
        writer.close();
    }

    public static void sample(JSAPResult JSAPconfig) {
        //sample size
        //sample years
        //sample

        //List<Foo> list = createItSomehow();
        //Collections.shuffle(list);
        //Foo foo = list.get(0);

        Set<Integer> numberSet = new HashSet<Integer>();
        Random random = new Random();

        while (numberSet.size() < 6) {
            numberSet.add(random.nextInt(49) + 1);
        }

        Integer[] intArray = new Integer[6];
        numberSet.toArray(intArray);
        Arrays.sort(intArray);

        StringBuilder sb = new StringBuilder();
        for (Integer i : intArray) {
            sb.append(i).append(" ");
        }

        String s = sb.toString().trim();
        System.out.println(s);
    }

}
