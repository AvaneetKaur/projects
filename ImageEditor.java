import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageEditor {
    /* Constants (Magic numbers) */
    private static final String PNG_FORMAT = "png";
    private static final String NON_RGB_WARNING =
            "Warning: we do not support the image you provided. \n" +
            "Please change another image and try again.";
    private static final String RGB_TEMPLATE = "(%3d, %3d, %3d) ";
    private static final int BLUE_BYTE_SHIFT = 0;
    private static final int GREEN_BYTE_SHIFT = 8;
    private static final int RED_BYTE_SHIFT = 16;
    private static final int ALPHA_BYTE_SHIFT = 24;
    private static final int BLUE_BYTE_MASK = 0xff << BLUE_BYTE_SHIFT;
    private static final int GREEN_BYTE_MASK = 0xff << GREEN_BYTE_SHIFT;
    private static final int RED_BYTE_MASK = 0xff << RED_BYTE_SHIFT;
    private static final int ALPHA_BYTE_MASK = ~(0xff << ALPHA_BYTE_SHIFT);
    private static final int NINETY_DEGREES = 90;
    private static final int ZERO = 0;
    private static final int ONE = 1;


    /* Static variables - DO NOT add any additional static variables */
    static int[][] image;

    public static void main(String[] args) throws IOException {
        load("fourcolor.png");
//        image = new int[3][3];
//        image[0][0] = 0; image[0][1] = 0; image[0][2] = 0;
//        image[1][0] = 2; image[1][1] = 0; image[1][2] = 0;
//        image[2][0] = 1; image[2][1] = 1; image[2][2] = 1;
//        int[][] patchedImage = open("khosla.png");
//        int patchedPixels = patch(50, 100, patchedImage, 255, 255, 255);
//        System.out.println(patchedPixels);
//        save("ucsd_patch_khosla.png");
        System.out.println("Hello World!");
        //rotate(180);

        printImage();
    }
    /**
     * Open an image from disk and return a 2D array of its pixels.
     * Use 'load' if you need to load the image into 'image' 2D array instead
     * of returning the array.
     *
     * @param pathname path and name to the file, e.g. "input.png",
     *                 "D:\\Folder\\ucsd.png" (for Windows), or
     *                 "/User/username/Desktop/my_photo.png" (for Linux/macOS).
     *                 Do NOT use "~/Desktop/xxx.png" (not supported in Java).
     * @return 2D array storing the rgb value of each pixel in the image
     * @throws IOException when file cannot be found or read
     */
    public static int[][] open(String pathname) throws IOException {
        BufferedImage data = ImageIO.read(new File(pathname));
        if (data.getType() != BufferedImage.TYPE_3BYTE_BGR &&
                data.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
            System.err.println(NON_RGB_WARNING);
        }
        int[][] array = new int[data.getHeight()][data.getWidth()];

        for (int row = 0; row < data.getHeight(); row++) {
            for (int column = 0; column < data.getWidth(); column++) {
                /* Images are stored by column major
                   i.e. (2, 10) is the pixel on the column 2 and row 10
                   However, in class, arrays are in row major
                   i.e. [2][10] is the 11th element on the 2nd row
                   So we reverse the order of i and j when we load the image.
                 */
                array[row][column] = data.getRGB(column, row) & ALPHA_BYTE_MASK;
            }
        }

        return array;
    }

    /**
     * Load an image from disk to the 'image' 2D array.
     *
     * @param pathname path and name to the file, see open for examples.
     * @throws IOException when file cannot be found or read
     */
    public static void load(String pathname) throws IOException {
        image = open(pathname);
    }

    /**
     * Save the 2D image array to a PNG file on the disk.
     *
     * @param pathname path and name for the file. Should be different from
     *                 the input file. See load for examples.
     * @throws IOException when file cannot be found or written
     */
    public static void save(String pathname) throws IOException {
        BufferedImage data = new BufferedImage(
                image[0].length, image.length, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < data.getHeight(); row++) {
            for (int column = 0; column < data.getWidth(); column++) {
                // reverse it back when we write the image
                data.setRGB(column, row, image[row][column]);
            }
        }
        ImageIO.write(data, PNG_FORMAT, new File(pathname));
    }

    /**
     * Unpack red byte from a packed RGB int
     *
     * @param rgb RGB packed int
     * @return red value in that packed pixel, 0 <= red <= 255
     */
    private static int unpackRedByte(int rgb) {
        return (rgb & RED_BYTE_MASK) >> RED_BYTE_SHIFT;
    }

    /**
     * Unpack green byte from a packed RGB int
     *
     * @param rgb RGB packed int
     * @return green value in that packed pixel, 0 <= green <= 255
     */
    private static int unpackGreenByte(int rgb) {
        return (rgb & GREEN_BYTE_MASK) >> GREEN_BYTE_SHIFT;
    }

    /**
     * Unpack blue byte from a packed RGB int
     *
     * @param rgb RGB packed int
     * @return blue value in that packed pixel, 0 <= blue <= 255
     */
    private static int unpackBlueByte(int rgb) {
        return (rgb & BLUE_BYTE_MASK) >> BLUE_BYTE_SHIFT;
    }

    /**
     * Pack RGB bytes back to an int in the format of
     * [byte0: unused][byte1: red][byte2: green][byte3: blue]
     *
     * @param red   red byte, must satisfy 0 <= red <= 255
     * @param green green byte, must satisfy 0 <= green <= 255
     * @param blue  blue byte, must satisfy 0 <= blue <= 255
     * @return packed int to represent a pixel
     */
    private static int packInt(int red, int green, int blue) {
        return (red << RED_BYTE_SHIFT)
                + (green << GREEN_BYTE_SHIFT)
                + (blue << BLUE_BYTE_SHIFT);
    }

    /**
     * Print the current image 2D array in (red, green, blue) format.
     * Each line represents a row in the image.
     */
    public static void printImage() {
        for (int[] ints : image) {
            for (int pixel : ints) {
                System.out.printf(
                        RGB_TEMPLATE,
                        unpackRedByte(pixel),
                        unpackGreenByte(pixel),
                        unpackBlueByte(pixel));
            }
            System.out.println();
        }
    }

    /**
     * Rotate the image by given degree.
     * @param degree
     */
    public static void rotate(int degree){
        if(degree <= 0 || degree % 90 != 0)
            return;
        int rotation = degree / 90;
        int[][] rotatedImage = new int[image[0].length][image.length];

        while(rotation != 0){
            for(int i = 0; i < image.length; i++){
                for(int j = 0; j < image[0].length; j++){
                    rotatedImage[j][(image.length - 1) - i] = image[i][j];
                }
            }
            int oldRow = image.length;
            int oldCol = image[0].length;
            image = rotatedImage;
            rotatedImage = new int[oldRow][oldCol];
            rotation--;
        }
    }

    /**
     * Shrink the image by given factors
     * @param heightScale
     * @param widthScale
     */
    public static void downSample(int heightScale, int widthScale){
        if(heightScale < 1 || heightScale > image.length
                || image.length % heightScale != 0)
            return;
        if(widthScale < 1 || widthScale > image[0].length
                || image[0].length % widthScale != 0)
            return;

        int finalHeight = image.length / heightScale;
        int finalWidth = image[0].length / widthScale;
        int [][] downsizedImage = new int[finalHeight][finalWidth];
        int sumRed = 0, averageRed;
        int sumGreen = 0, averageGreen;
        int sumBlue = 0, averageBlue;
        int total = heightScale * widthScale;

        // collecting individual RGBs in each pixel of original image
        for(int row = 0; row < image.length; row += heightScale){
            for(int col = 0; col < image[0].length; col += widthScale) {
                for (int i = row; i < row + heightScale; i++) {
                    for (int j = col; j < col + widthScale; j++) {
                        sumRed += unpackRedByte(image[i][j]);
                        sumBlue += unpackBlueByte(image[i][j]);
                        sumGreen += unpackGreenByte(image[i][j]);
                    }
                }
                averageRed = sumRed / total;
                averageBlue = sumBlue / total;
                averageGreen = sumGreen / total;

                downsizedImage[row/heightScale][col/widthScale]
                        = packInt(averageRed, averageGreen, averageBlue);
                sumRed = 0;
                sumGreen = 0;
                sumBlue = 0;
            }
        }
        image = downsizedImage;
    }

    /**
     * Patch a image on the original image at the given index
     * @param startRow
     * @param startColumn
     * @param patchImage
     * @param transparentBlue
     * @param transparentGreen
     * @param transparentRed
     * @return int counter number of pixels patched
     */
    public static int patch(int startRow, int startColumn,
                            int[][] patchImage, int transparentRed,
                            int transparentGreen, int transparentBlue){

        if(startRow < 0 || startRow >= image.length)
            return 0;
        if(startColumn < 0 || startColumn >= image[0].length)
            return 0;
        int endRow = startRow + (patchImage.length - 1);
        int endCol = startColumn + (patchImage[0].length - 1);
        if(endRow >= image.length)
            return 0;
        if(endCol >= image[0].length)
            return 0;
        int counter = 0;
        int redByte, greenByte, blueByte;

        for(int i = 0; i < patchImage.length; i++){
            for(int j = 0; j < patchImage[0].length; j++){
                redByte = unpackRedByte(patchImage[i][j]);
                greenByte = unpackGreenByte(patchImage[i][j]);
                blueByte = unpackBlueByte(patchImage[i][j]);
                if(redByte == transparentRed
                        && greenByte == transparentGreen
                        && blueByte == transparentBlue){
                    continue;
                }
                image[startRow + i][startColumn + j] = patchImage[i][j];
                counter++;
            }
        }
        return counter;
    }

}