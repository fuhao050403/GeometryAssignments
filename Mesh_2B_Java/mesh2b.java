/*************************************************************
* Java program for subject 2B
* Student: Fu Hao(20N8100013G)
* Chuo University
* Information and System Engineering
* Date: 2020/5/2
**************************************************************/

import java.io.*;
import java.util.ArrayList;

class filePrinter
{
    public static void saveDataToFile(ArrayList<String> res) throws IOException
    {
        try
        {
            File file = new File(System.getProperty("user.home") + "/Desktop", "mesh2A.obj");
            // Create mesh2B.obj file to the desktop
            if(file.createNewFile()) {
                System.out.println("File created: " + file.getName()); }
            else
            {
                file.delete();
                if(file.createNewFile()) { System.out.println("Deleted existed file and created a new one."); }
            }
        }
        catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        
        // Allocate memory space for PrintWriter instance which used for writing date into file
        PrintWriter pw = new PrintWriter(System.getProperty("user.home") + "/Desktop/mesh2B.obj");

        for(int i = 0; i < res.size(); i++)
        {
            pw.println(res.get(i));
        }
        pw.close();
    }
}

class mesh2b
{
    public static void main(String[] args)
    {
        // Result data which contain all context will be written into result file
        ArrayList<String> result = new ArrayList<String>();

        // Initialize calculational coefficient
        double R = 1.0f;
        double r = 0.5f;
        int phiSegments = 100;
        int thetaSegements = 100;
        double phiOffset = (Math.PI * 2) / phiSegments;
        double thetaOffset = (Math.PI * 2) / thetaSegements;

        // Calculate Vertices
        for(int i = 0; i < phiSegments; i++)
        {
            for(int j = 0; j < thetaSegements; j++)
            {
                double phi   = i * phiOffset;
                double theta = j * thetaOffset;

                double x = (R + r * (1 + (Math.cos(8 * phi + theta) / 8)) * Math.cos(phi)) * Math.cos(theta);
                double y = (R + r * (1 + (Math.cos(8 * phi + theta) / 8)) * Math.cos(phi)) * Math.sin(theta);
                double z = r * (1 + (Math.cos(8 * phi + theta) / 8)) * Math.sin(phi);

                String str = "v "  
                           + Double.toString(x) + " "
                           + Double.toString(y) + " "
                           + Double.toString(z);

                //Store vertice data to Arraylist
                result.add(str);
            }
        }

        // Calculate faces
        for(int i = 0; i < phiSegments; i++)
        {
            for(int j = 0; j < thetaSegements; j++)
            {
                int v1, v2, v3, v4;

                // Calcuate v1
                v1 = j + i * thetaSegements;
                // Calcuate v2
                if(j == thetaSegements - 1) { v2 = v1 - (phiSegments - 1); }
                else { v2 = v1 + 1; }
                // Calcuate v3 and v4
                if(i == phiSegments - 1)
                { 
                    v3 = v2 + phiSegments - phiSegments * thetaSegements;
                    v4 = v1 + phiSegments - phiSegments * thetaSegements;
                }
                else
                {
                    v3 = v2 + phiSegments;
                    v4 = v1 + phiSegments;
                }
                
                // Add 1 to every value due to index of array starts by 0
                v1++;
                v2++;
                v3++;
                v4++;

                //Store face data to Arraylist
                String str = "f " + v1 + " " + v2 + " " + v3 + " " + v4;
                result.add(str);
            }
        }

        // Save vertices and faces data to mesh2A.obj file
        try
        {
            filePrinter.saveDataToFile(result);
        }
        catch(IOException exc)
        {
            System.out.println("Error: " + exc.getMessage());
        }
    }
}