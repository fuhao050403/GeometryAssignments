/*************************************************************
* Java program for assignment 3B
* Student: Fu Hao(20N8100013G)
* Chuo University
* Information and System Engineering(Makino Lab)
* Date: 2020/5/8
**************************************************************/

import java.io.*;
import java.util.Scanner;

class fileManager
{
    public static void CreateResultFile() throws IOException
    {
        try
        {
            File file = new File(System.getProperty("user.home") + "/Desktop", "Result.txt");
            // Create Result.txt file to the desktop
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

class assign3B
{
    public static void main(String[] args) throws FileNotFoundException
    {
        // Read data from file "lion.off" on Desktop
        File file = new File(System.getProperty("user.home") + "/Desktop", "lion.off");
        if(!file.exists()) { System.out.println("lion.off is not existed on desktop."); return; }
        Scanner s = new Scanner(file);
        s.nextLine(); // Skip first line of context

        // Get number of vertices and faces which lion.off has
        String str_num_of_vertices = s.next();
        int num_of_vertices = Integer.parseInt(str_num_of_vertices);
        String str_num_of_faces = s.next();
        int num_of_faces = Integer.parseInt(str_num_of_faces);

        s.nextLine(); // Skip '0' at the end of 2th line

        // Storing vertices
        double[][] vertex = new double[num_of_vertices][3];
        for(int i = 0; i < num_of_vertices; i++)
        {
            vertex[i][0] = s.nextDouble();
            vertex[i][1] = s.nextDouble();
            vertex[i][2] = s.nextDouble();
        }

        // Define variable 'vertices' for storing all vertices value that lion.off has
        int[][] vertices = new int[num_of_faces][3];
        for(int i = 0; i < num_of_faces; i++) // O(3 * n) = O(n)
        {
            s.next(); // Skip first 'number of vertice' part of current line
            vertices[i][0] = s.nextInt();
            vertices[i][1] = s.nextInt();
            vertices[i][2] = s.nextInt();
        }
        s.close(); // Close lion.off filestream

        int good_try = 0; // Count how many face is integrated
        int bad_edge = 0; // Count how many unintegrated edge is existing
        int[][] v_index = { {0, 1}, {1, 2}, {2, 0} }; // define vertex sequence of all three edges in one face

        /*************************************************************
        * Time complexity: O(3n + (3n)^2 + 3*3*n) = O(9n^2 + 12n) = O(n^2)
        **************************************************************/

        // Check mesh is integrated or not
        for(int i = 0; i < num_of_faces; i++) // O(n)
        {
            // Calculate every face one by one

            int number_of_integrated_faces = 0; // maximum value is 3 which all three faces around is integrated with current fact
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
                    if(_v1 == v2 && _v2 == v1) { number_of_integrated_faces++; is_integrated_edge = true; continue; }
                    else if(_v3 == v1 && _v2 == v2) { number_of_integrated_faces++; is_integrated_edge = true; continue; }
                    else if(_v1 == v1 && _v3 == v2) { number_of_integrated_faces++; is_integrated_edge = true; continue; }
                }

                if(!is_integrated_edge) { bad_edge++; }
            }

            // If find 3 matched face,which means current face is integrated with all three faces around
            if(number_of_integrated_faces == 3) { good_try++; }
        }

        /*************************************************************
        * Calculate Signed Volnme
        * Set arbitrary point P as (0, 0, 0)
        **************************************************************/
        double volnme = 0f;
        for(int i = 0; i < num_of_faces; i++)
        {
            // Get all 3 vertices(9 value for XYZ) of current face
            double[][] v = new double[3][3];
            
            for(int row = 0; row < 3; row++)
            {
                for(int col = 0; col < 3; col++)
                {
                    v[row][col] = vertex[vertices[i][row]][col];
                }
            }

            // Calculate Determinant
            double det = 
                  v[0][0] * v[1][1] * v[2][2]
                + v[0][1] * v[1][2] * v[2][0]
                + v[1][0] * v[2][1] * v[0][2]
                - v[0][2] * v[1][1] * v[2][0]
                - v[0][0] * v[1][2] * v[2][1]
                - v[0][1] * v[1][0] * v[2][2];
            
            // Add to final result
            volnme += det / 6;
        }

        boolean all_facing_are_right = false;
        if(volnme > 0) { all_facing_are_right = true; }

        // Create result file(mesh2C.off)
        try { fileManager.CreateResultFile(); }
        catch(IOException exc) { System.out.println("Error: " + exc.getMessage()); }
        
        // Initializing PrintWriter instance for writing data to result file
        PrintWriter pw = new PrintWriter(System.getProperty("user.home") + "/Desktop/Result.txt");

        String res_str =
            "メッシュのファイル名: " + file.getName() + "\n" +
            "頂点数: " + num_of_vertices + "\n" +
            "面数: " + num_of_faces + "\n" +
            "整合的な面数: " + good_try + "\n" +
            "整合的でない面数: " + bad_edge + "\n";

        if((good_try == num_of_faces) && all_facing_are_right) // Output result file
        {
            System.out.println("This mesh is integrated and correct facing!");
            res_str +=
                "符号付き体積値: " + volnme + "\n" +
                "結論: 本メッシュは整合的であることと、メッシュの面の向きが正しいことがわかった." + "\n\n";
        }
        else if((good_try == num_of_faces) && !all_facing_are_right)
        {
            System.out.println("This mesh is integrated, but isn't correct facing!");
            res_str +=
                "符号付き体積値: " + volnme + "\n" +
                "結論: 本メッシュは整合的であるが、メッシュの面の向きが正しくないことがわかった." + "\n\n";
        }
        else if(good_try != num_of_faces)
        {
            System.out.println("This mesh is not integrated!");
            res_str += "結論: 本メッシュは整合的でないため、メッシュの面の向きについて判断はしない." + "\n\n";
        }

        res_str += "計算複雑度(最悪のアルゴリズムによる): O(3n + (3n)^2 + 3*3*n) = O(9n^2 + 12n) = O(n^2)";
        
        pw.println(res_str);
        pw.close();
    }
}