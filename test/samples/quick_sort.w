func quickSort(nums:[], lo:int, hi:int):[] {
   if (lo >= hi) {
       return nums
   }

   var p = 0
   var x = nums[hi]
   var j = lo
   var i = lo - 1
   while (j < hi) {
       if (nums[j] <= x) {
           i = i + 1
           var temp = nums[j]
           nums[j] = nums[i]
           nums[i] = temp
       }
       j = j + 1
   }
   p = i+1
   var temp = nums[p]
   nums[p] = nums[hi]
   nums[hi] = temp

   var l = quickSort(nums, lo, p-1)
   var r = quickSort(l, p+1, hi)

   return r
}

var v = [1,3,4,5,2,100,99,0,-1]
print(quickSort(v, 1, 9))