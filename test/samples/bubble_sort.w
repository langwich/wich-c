func bubbleSort(vec:[]):[] {
	var length = len(vec)
	var v = vec
	var i = 1
	var j = 1
	while(i<= length){
		while(j<=((length - i))){
			if (v[j] > v[j+1]){
				var swap = v[j]
				v[j] = v[j+1]
				v[j+1] = swap
			}
			j = j+1
		}
		i = i+1
	}
	return v
}

var x = [1,4,2,3]
print(bubbleSort(x))