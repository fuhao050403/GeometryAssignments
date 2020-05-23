/*************************************************************
* Course: Geometry Processing
* Java program for assignment 5A
* Student: Fu Hao(20N8100013G)
* Chuo University
* Information and System Engineering(Makino Lab)
* Date: 2020/5/23
**************************************************************/

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class FileManager
{
    public static void CreateResultFile() throws IOException
    {
        try
        {
            File file = new File(System.getProperty("user.home") + "/Desktop", "result.txt");
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

class MergeSort
{
    private static void merge(int arr[][], int left, int mid, int right)
    {
        int num1 = mid - left + 1;
        int num2 = right - mid;

        int arr_L[][] = new int[num1][5];
        int arr_R[][] = new int[num2][5];

        // Copy data to temp array
        for (int i = 0; i < num1; i++) { arr_L[i] = arr[left + i]; }
        for (int j = 0; j < num2; j++) { arr_R[j] = arr[mid + 1 + j]; }

        int i = 0, j = 0;
        int num_comp = left; // Counting how many comparison has been done

        while (i < num1 && j < num2)
        {
            if(arr_L[i][2] <= arr_R[j][2]) { arr[num_comp] = arr_L[i]; i++; }
            else { arr[num_comp] = arr_R[j]; j++; }
            num_comp++;
        }

        while (i < num1) { arr[num_comp] = arr_L[i]; i++; num_comp++; }
        while (j < num2) { arr[num_comp] = arr_R[j]; j++; num_comp++; }
    }

    public static void sort(int arr[][], int left, int right)
    {
        // Start iteration
        if (left < right)
        {
            int mid = (left + right) / 2;

            sort(arr, left, mid);
            sort(arr , mid + 1, right);

            merge(arr, left, mid, right);
        } 
    }
}

class BinarySearch
{
    public static int binarySearch(int[][] arr_src, int lookFor)
    {
        int start = 0, end = arr_src.length - 1;
        int mid = (start + end) / 2; // Initial to the middle position
        boolean isFound = false;

        // For mesh with no hole inside otherwise loop won't stop and causing fatal bug
        while(!isFound)
        {
            if(arr_src[mid][2] == lookFor){ isFound = true; }
            else if(arr_src[mid][2] < lookFor)
            {
                start = mid;
                mid = (start + end) / 2;
            }
            else
            {
                end = mid;
                mid = (start + end) / 2;
            }
        }

        return mid;
    }
}

class Assign5A
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
        for(int i = 0; i < num_of_vertices; i++) { s.nextLine(); }

        int[][] edge_info = new int[num_of_faces * 3][5];
        //format: [0]index of edge, [1]index of face that current edge belongs to, [2]v_src, [3]v_tgt, [4]h_opp(-1 if not found)

        for(int i = 0; i < num_of_faces; i++)
        {
            s.next();
            int[] vertex = new int[3];

            for(int j = 0; j < 3; j++) { vertex[j] = s.nextInt(); }
            for(int k = 0; k < 3; k++)
            {
                edge_info[i * 3 + k][0] = i * 3 + k;
                edge_info[i * 3 + k][1] = i;
                switch(k)
                {
                    case 0:
                        edge_info[i * 3 + k][2] = vertex[1];
                        edge_info[i * 3 + k][3] = vertex[2];
                    break;
                    case 1:
                        edge_info[i * 3 + k][2] = vertex[2];
                        edge_info[i * 3 + k][3] = vertex[0];
                    break;
                    case 2:
                        edge_info[i * 3 + k][2] = vertex[0];
                        edge_info[i * 3 + k][3] = vertex[1];
                    break;
                }
                edge_info[i * 3 + k][4] = -1;
            }
        }

        s.close();

        // Sorting by v_src value(start from 0) with Time complexity: O(m * log(m))
        MergeSort.sort(edge_info, 0, edge_info.length - 1);

        // Find h.opp of every edge by using Binary Search with total Time complexity: O(m * log(m))
        for(int i = 0; i < edge_info.length; i++)
        {
            int _opp_src = BinarySearch.binarySearch(edge_info, edge_info[i][3]);

            int _tmp_index = _opp_src;
            while((_tmp_index > -1) && (edge_info[_tmp_index][2] == edge_info[i][3]))
            {
                if(edge_info[_tmp_index][3] == edge_info[i][2])
                {
                    edge_info[i][4] = edge_info[_tmp_index][0];
                    break;
                }
                _tmp_index--;
            }
            _tmp_index = _opp_src + 1;
            while((_tmp_index < edge_info.length) && (edge_info[_tmp_index][2] == edge_info[i][3]))
            {
                if(edge_info[_tmp_index][3] == edge_info[i][2])
                {
                    edge_info[i][4] = edge_info[_tmp_index][0];
                    break;
                }
                _tmp_index++;
            }
        }

        // Create result file(lion_opp.txt)
        try { FileManager.CreateResultFile(); }
        catch(IOException exc) { System.out.println("Error: " + exc.getMessage()); }
        
        // Initializing PrintWriter instance for writing data to result file
        PrintWriter pw = new PrintWriter(System.getProperty("user.home") + "/Desktop/result.txt");
        String str_out = 
            "時間複雑度: O(n + m * 6 + m * log(m) + m * log(m) + m) = O(mlog(m))\n\n" + 
            "v_src     v_tgt     h_out     h_outのh_opp     h_outの所属面f\n" +
            "------------------------------------------------------------";
        pw.println(str_out);
        for(int i = 0; i < edge_info.length; i++)
        {
            //format: [0]index of edge, [1]index of face that current edge belongs to, [2]v_src, [3]v_tgt, [4]h_opp(-1 if not found)
            str_out =
                Integer.toString(edge_info[i][2]) + addSpace(10 - Integer.toString(edge_info[i][2]).length()) +
                Integer.toString(edge_info[i][3]) + addSpace(10 - Integer.toString(edge_info[i][3]).length()) +
                Integer.toString(edge_info[i][0]) + addSpace(10 - Integer.toString(edge_info[i][0]).length()) +
                Integer.toString(edge_info[i][4]) + addSpace(17 - Integer.toString(edge_info[i][4]).length()) +
                Integer.toString(edge_info[i][1]);
            pw.println(str_out);
        }
        
        pw.close();
    }

    static String addSpace(int _s)
    {
        String s = "";

        for(int i = 0; i < _s; i++) { s += " "; }

        return s;
    }
}