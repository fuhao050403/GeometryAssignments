/*************************************************************
* Java program for subject 2C
* Student: Fu Hao(20N8100013G)
* Chuo University
* Information and System Engineering
* Date: 2020/5/2
**************************************************************/

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

class fileManager
{
    public static void CreateResultFile() throws IOException
    {
        try
        {
            File file = new File(System.getProperty("user.home") + "/Desktop", "mesh2C.off");
            // Create mesh2C.off file to the desktop
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
    }
}

class mesh2c
{
    public static void main(String[] args) throws FileNotFoundException
    {
        // Read data from file "lion.off" on Desktop
        File file = new File(System.getProperty("user.home") + "/Desktop", "lion.off");
        Scanner s = new Scanner(file);
        s.nextLine(); // Skip first line of context

        // Create result file(mesh2C.off)
        try { fileManager.CreateResultFile(); }
        catch(IOException exc) { System.out.println("Error: " + exc.getMessage()); }
        
        // Initializing PrintWriter instance for writing data to result file
        PrintWriter pw = new PrintWriter(System.getProperty("user.home") + "/Desktop/mesh2C.off");
        pw.println("OFF"); // Write first line to mesh2C.off

        // Get number of vertices which lion.off has
        String str_num_of_vertices = s.next();
        String str_num_of_faces    = s.next();
        int num_of_vertices = Integer.parseInt(str_num_of_vertices);
        int num_of_faces    = Integer.parseInt(str_num_of_faces);
        pw.println(Integer.toString(num_of_vertices + 8) + " " + Integer.toString(num_of_faces + 6) + s.nextLine());

        // Extract max and min value of each axis(X, Y, Z) separately
        double minX = 0, maxX = 0;
        double minY = 0, maxY = 0;
        double minZ = 0, maxZ = 0;

        for(int i = 0; i < num_of_vertices; i++)
        {
            double x = Double.parseDouble(s.next());
            if(x < minX) { minX = x; }
            if(x > maxX) { maxX = x; }
            double y = Double.parseDouble(s.next());
            if(y < minY) { minY = y; }
            if(y > maxY) { maxY = y; }
            double z = Double.parseDouble(s.next());
            if(z < minZ) { minZ = z; }
            if(z > maxZ) { maxZ = z; }

            String tmp = Double.toString(x) + " " + Double.toString(y) + " " + Double.toString(z);
            pw.println(tmp); 
        }

        // Calculate and create String arrays for Vertices and faces
        String vStr[] = new String[8];
        String fStr[] = new String[6];

        vStr[0] = Double.toString(minX) + " " + Double.toString(maxY) + " " + Double.toString(maxZ);
        vStr[1] = Double.toString(minX) + " " + Double.toString(minY) + " " + Double.toString(maxZ);
        vStr[2] = Double.toString(maxX) + " " + Double.toString(minY) + " " + Double.toString(maxZ);
        vStr[3] = Double.toString(maxX) + " " + Double.toString(maxY) + " " + Double.toString(maxZ);
        vStr[4] = Double.toString(minX) + " " + Double.toString(maxY) + " " + Double.toString(minZ);
        vStr[5] = Double.toString(minX) + " " + Double.toString(minY) + " " + Double.toString(minZ);
        vStr[6] = Double.toString(maxX) + " " + Double.toString(minY) + " " + Double.toString(minZ);
        vStr[7] = Double.toString(maxX) + " " + Double.toString(maxY) + " " + Double.toString(minZ);

        for(int i = 0; i < 8; i++) { pw.println(vStr[i]); }
        
        // Calculate face list and write into result file
        String id_quad_1 = Integer.toString(num_of_vertices);
        String id_quad_2 = Integer.toString(num_of_vertices + 1);
        String id_quad_3 = Integer.toString(num_of_vertices + 2);
        String id_quad_4 = Integer.toString(num_of_vertices + 3);
        String id_quad_5 = Integer.toString(num_of_vertices + 4);
        String id_quad_6 = Integer.toString(num_of_vertices + 5);
        String id_quad_7 = Integer.toString(num_of_vertices + 6);
        String id_quad_8 = Integer.toString(num_of_vertices + 7);

        fStr[0] = "4 " + id_quad_1 + " " + id_quad_4 + " " + id_quad_3 + " " + id_quad_2;
        fStr[1] = "4 " + id_quad_5 + " " + id_quad_6 + " " + id_quad_7 + " " + id_quad_8;
        fStr[2] = "4 " + id_quad_1 + " " + id_quad_5 + " " + id_quad_8 + " " + id_quad_4;
        fStr[3] = "4 " + id_quad_3 + " " + id_quad_7 + " " + id_quad_6 + " " + id_quad_2;
        fStr[4] = "4 " + id_quad_2 + " " + id_quad_6 + " " + id_quad_5 + " " + id_quad_1;
        fStr[5] = "4 " + id_quad_4 + " " + id_quad_8 + " " + id_quad_7 + " " + id_quad_3;

        // Copy face data from original lion.off file to result file
        s.nextLine();
        for(int i = 0; i < num_of_faces; i++) { pw.println(s.nextLine()); }
        
        // Add new face data at the bottom of result file
        for(int i = 0; i < 6; i++) { pw.println(fStr[i]); }

        // Terminate filestream
        s.close();
        pw.close();
    }
}