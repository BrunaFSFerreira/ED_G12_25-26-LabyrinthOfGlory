package main.sort;

public class SortLinkedList {

    // SELECTION SORT
    public static <T extends Comparable<? super T>> void selectionSort(T[] data) {
        int min;
        T temp;

        for (int index = 0; index < data.length - 1; index++) {
           min = index;
           for (int scan = index + 1 ; scan < data.length; scan++){
               if (data[scan].compareTo(data[min]) < 0) {
                   min = scan;
               }

               // Swap the values
               temp = data[min];
               data[min] = data[index];
               data[index] = temp;
           }
        }
    }

    // INSERTION SORT
    public static <T extends Comparable<? super T>> void insertionSort(T[] data) {
        for (int index = 1; index < data.length; index++) {
            T key = data[index];
            int position = index;

            // Shift larger values to the right
            while (position > 0 && data[position - 1].compareTo(key) > 0) {
                data[position] = data[position - 1];
                position--;
            }

            data[position] = key;
        }
    }

    // BUBBLE SORT
    public static <T extends Comparable<? super T>> void bubbleSort(T[] data) {
        int possition, scan;
        T temp;

        for (possition = data.length - 1; possition > 0; possition--) {
            for (scan = 0; scan < possition; scan++) {
                if (data[scan].compareTo(data[scan + 1]) > 0) {
                    // Swap the values
                    temp = data[scan];
                    data[scan] = data[scan + 1];
                    data[scan + 1] = temp;
                }
            }
        }
    }

    // QUICK SORT
    public static <T extends Comparable<? super T>> void quickSort(T[] data, int min, int max){
        int indexofpartition;

        if (max - min > 0){
            //Create partitions
            indexofpartition = findPartition(data, min, max);

            //Sort the left side
            quickSort(data, min, indexofpartition - 1);

            //Sort the right side
            quickSort(data, indexofpartition + 1, max);
        }
    }

    private static <T extends Comparable<? super T>> int findPartition(T[] data, int min, int max) {
        int left, right;
        T temp, partitionelement;
        int middle = (min + max) / 2;

        //Use middle element as partition
        partitionelement = data[middle];
        left = min;
        right = max;

        while (left < right) {
            //Search for an element that is > the partition element
            while (data[left].compareTo(partitionelement) < 0) {
                left++;
            }

            //Search for an element that is < the partition element
            while (data[right].compareTo(partitionelement) > 0) {
                right--;
            }

            //Swap the elements
            if (left < right) {
                temp = data[left];
                data[left] = data[right];
                data[right] = temp;
            }
        }

        //Move partition element to partition index
        temp = data[min];
        data[min] = data[right];
        data[right] = temp;

        return right;
    }

    // MERGE SORT
    public static <T extends Comparable<? super T>> void mergeSort(T[] data, int min, int max) {
        T[] temp;
        int index1, left, right;

        //Return on list of length one
        if (min == max){
            return;
        }

        //Find the lenght and the midpoint of the list
        int size = max - min + 1;
        int pivot = (min + max) / 2;
        temp = (T[]) new Comparable[size];
        mergeSort(data, min, pivot); //Sort left half of list
        mergeSort(data, pivot + 1, max); //Sort right half of list

        //Copy sorted data into workspace
        for (index1 = 0; index1 < size; index1++) {
            temp[index1] = data[min + index1];
        }

        //Merge the two sorted lists
        left = 0;
        right = pivot - min + 1;
        for(index1 = 0; index1 < size; index1++){
            if (right <= max - min){
                if (left <= pivot - min){
                    if (temp[left].compareTo(temp[right]) > 0){
                        data[index1 + min] = temp[right++];
                    } else {
                        data[index1 + min] = temp[left++];
                    }
                } else {
                    data[index1 + min] = temp[right];
                }
            } else {
                data[index1 + min] = temp[left++];
            }
        }

    }

}
