func bubbleSort(v:[]):[] {
	var length = len(v)
	var i = 1
	var j = 1
	while(i<= length){
		j = 1
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

var x = [100,99,4,2.15,2,23,3]
print(bubbleSort(x))