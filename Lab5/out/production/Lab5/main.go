package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

func main() {
	rand.Seed(time.Now().UnixNano())

	arrays := [3][]int{
		{rand.Intn(1000), rand.Intn(1000), rand.Intn(1000)},
		{rand.Intn(1000), rand.Intn(1000), rand.Intn(1000)},
		{rand.Intn(1000), rand.Intn(1000), rand.Intn(1000)},
	}

	var mu sync.Mutex
	var wg sync.WaitGroup

	for i := 0; i < 3; i++ {
		wg.Add(1)
		go func(index int) {
			defer wg.Done()

			for {

				time.Sleep(10 * time.Millisecond)

				mu.Lock()
				sums := [3]int{
					sumOfArray(arrays[0]),
					sumOfArray(arrays[1]),
					sumOfArray(arrays[2]),
				}

				if sums[0] == sums[1] && sums[1] == sums[2] {
					mu.Unlock()
					break
				}

				if sums[index] < sums[(index+1)%3] || sums[index] < sums[(index+2)%3] {
					arrays[index][rand.Intn(len(arrays[index]))]++
				} else {
					arrays[index][rand.Intn(len(arrays[index]))]--
				}
				mu.Unlock()
			}
		}(i)
	}

	wg.Wait()

	for i, arr := range arrays {
		fmt.Printf("Масив %d: %v, Сума: %d\n", i, arr, sumOfArray(arr))
	}
}

func sumOfArray(arr []int) int {
	sum := 0
	for _, v := range arr {
		sum += v
	}
	return sum
}
