/* A bitmapped memory allocator. One bit in bitmap for each 32-bit word in heap
 * indicates whether the word is in use (1) or free (0). We need a second bitmap
 * for freeing memory that indicates whether a word is the start of a block.
 * Without this information, we don't know when to stop turning off bits in
 * the in-use bitmap. The other choice is to track a size field in each object,
 * but then we might as well use binning or something else. See page 42 in
 * "Dynamic Storage Allocation, A Survey and Critical Review" by Wilson, Johnstone, Neely, Boles
 * http://www.cs.northwestern.edu/~pdinda/icsclass/doc/dsa.pdf
 * "In the most obvious scheme, two bitmaps are required (one to encode the
 *  boundaries of blocks, and another to encode whether blocks are in use), ..."
 */

/* From Dynamic Storage Allocation paper, "a bitmap can be quickly scanned
 * a byte at a time using a 256-way lookup table to detect whether there are
 * any runs of a desired length."
 */
static int runs0[256] = { // how many runs of 0 bits for a value of i=0.255?
// 0 runs   i
	255, // 0
	254, // 1
};

static int *inuse;

void f() {

}