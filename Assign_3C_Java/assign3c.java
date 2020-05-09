/*************************************************************
* Java program for assignment 3C
* Student: Fu Hao(20N8100013G)
* Chuo University
* Information and System Engineering(Makino Lab)
* Date: 2020/5/9
**************************************************************/

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class fileManager
{
    public static void CreateResultFile() throws IOException
    {
        try
        {
            File file = new File(System.getProperty("user.home") + "/Desktop", "lion_fixed.off");
            // Create result file to the desktop
            if(file.createNewFile())
            {
                System.out.println("File created: " + file.getName());
            }
            else
            {
                file.delete();
                if(file.createNewFile())
                { 
                    System.out.println("File created: " + file.getName());
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

class assign3C
{
    public static void main(String[] args) throws FileNotFoundException
    {
        // Read data from file "lion_holed.off" on Desktop
        File file = new File(System.getProperty("user.home") + "/Desktop", "lion_holed.off");
        if(!file.exists()) { System.out.println("lion_hollowed.off is not existed on desktop."); return; }
        Scanner s = new Scanner(file);
        s.nextLine(); // Skip first line of context

        // Get number of vertices and faces which lion.off has
        String str_num_of_vertices = s.next();
        int num_of_vertices = Integer.parseInt(str_num_of_vertices);
        String str_num_of_faces = s.next();
        int num_of_faces = Integer.parseInt(str_num_of_faces);

        s.nextLine(); // Skip '0' at the end of 2th line

        ArrayList<String> data_to_write = new ArrayList<String>(); // Initialize ArrayList for data which would write into result file
        data_to_write.add("OFF");
        // Storing vertices
        double[][] vertex = new double[num_of_vertices][3];
        for(int i = 0; i < num_of_vertices; i++)
        {
            vertex[i][0] = s.nextDouble();
            vertex[i][1] = s.nextDouble();
            vertex[i][2] = s.nextDouble();
            data_to_write.add(Double.toString(vertex[i][0]) + " " + Double.toString(vertex[i][1]) + " " + Double.toString(vertex[i][2]));
        }

        // Define variable 'vertices' for storing all vertices value that lion.off has
        int[][] vertices = new int[num_of_faces][3];
        for(int i = 0; i < num_of_faces; i++) // O(3 * n) = O(n)
        {
            String _tmp = s.next(); // Skip first 'number of vertice' part of current line
            vertices[i][0] = s.nextInt();
            vertices[i][1] = s.nextInt();
            vertices[i][2] = s.nextInt();
            _tmp += " " + Integer.toString(vertices[i][0]) + " " + Integer.toString(vertices[i][1]) + " " + Integer.toString(vertices[i][2]);
            data_to_write.add(_tmp);
        }
        s.close(); // Close lion.off filestream

        ArrayList<String> bad_edge = new ArrayList<String>(); // Storing bad edge vertices
        int[][] v_index = { {0, 1}, {1, 2}, {2, 0} }; // define vertex sequence of all three edges in one face

        /*************************************************************
        * Detect unintegrated faces
        **************************************************************/
        for(int i = 0; i < num_of_faces; i++) // O(n)
        {
            // Calculate every face one by one

            for(int j = 0; j < 3; j++) // 3 times( O(3) = O(1) )
            {
                // Every edge of current triangle face(3 edges)

                int v1 = vertices[i][v_index[j][0]];
                int v2 = vertices[i][v_index[j][1]];

                boolean is_integrated_edge = false;

                for(int n = 0; n < num_of_faces; n++) // O(n)
                {
                    // Run thought all edges and try to find the matched edge

                    int _v1 = vertices[n][0];
                    int _v2 = vertices[n][1];
                    int _v3 = vertices[n][2];

                    // Check if integrated edge is existing or not
                    if(_v1 == v2 && _v2 == v1) { is_integrated_edge = true; continue; }
                    else if(_v3 == v1 && _v2 == v2) { is_integrated_edge = true; continue; }
                    else if(_v1 == v1 && _v3 == v2) { is_integrated_edge = true; continue; }
                }

                if(!is_integrated_edge)
                {
                    bad_edge.add(v1 + " " + v2);
                }
            }
        }

        /*************************************************************
        * Detect holes
        **************************************************************/
        ArrayList<String> hole_vertex_loop = new ArrayList<String>();

        String[] starting_vertices = bad_edge.get(0).split(" ");
        String number_search_for = starting_vertices[1];

        String loop = number_search_for;
        bad_edge.remove(0);

        // Find connected edges(hole_vertex_loop) and calculate number of holes
        while(bad_edge.size() > 1)
        {
            for(int i = 0; i < bad_edge.size(); i++)
            {
                String[] _tmp = bad_edge.get(i).split(" ");

                if(number_search_for.equals(_tmp[0]))
                {
                    if(_tmp[1].equals(starting_vertices[0]))
                    {
                        hole_vertex_loop.add(starting_vertices[0] + " " + loop);
                        bad_edge.remove(i);

                        starting_vertices = bad_edge.get(0).split(" ");
                        number_search_for = starting_vertices[1];
                        loop = starting_vertices[1];
                    }
                    else
                    {
                        loop = _tmp[1] + " " + loop;
                        number_search_for = _tmp[1];
                        bad_edge.remove(i);
                    }
                    
                }
            }
        }

        /*************************************************************
        * Fix existing holes(2 holes in this case)
        **************************************************************/
        int num_edge_to_add = 0;
        for(int i = 0; i < hole_vertex_loop.size(); i++)
        {
            String[] v = hole_vertex_loop.get(i).split(" ");
            for(int j = 0; j < v.length - 2; j++)
            {
                data_to_write.add("3 " + v[j] + " " + v[j + 1] + " " + v[v.length - 1]);
                num_edge_to_add++;
            }
        }
        data_to_write.add(1, str_num_of_vertices + " " + Integer.toString(num_of_faces + num_edge_to_add) + " 0");

        // Create result file(mesh2C.off)
        try { fileManager.CreateResultFile(); }
        catch(IOException exc) { System.out.println("Error: " + exc.getMessage()); }
        
        // Initializing PrintWriter instance for writing data to result file
        PrintWriter pw = new PrintWriter(System.getProperty("user.home") + "/Desktop/lion_fixed.off");
        for(int i = 0; i < data_to_write.size(); i++)
        {
            pw.println(data_to_write.get(i));
        }

        pw.close();
    }
}