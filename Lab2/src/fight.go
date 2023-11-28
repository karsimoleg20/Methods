package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

// Цілочисельний масив, що зберігає кількість енергії Ци кожного ченця.
var energy []int

// Функція, яка проводить бій між двома ченцями і повертає переможця.
func duel(c1, c2 int) int {
	if energy[c1] > energy[c2] {
		return c1
	}
	return c2
}

// Функція для змагань між ченцями на Шляху Кулака.
func competition(start, end int, results chan int, wg *sync.WaitGroup) {
	defer wg.Done()

	if start == end {
		results <- start // Якщо лише один ченец, він переможе
		return
	}

	mid := (start + end) / 2

	// Запускаємо підзадачі для лівої та правої половини масиву.
	leftResults := make(chan int)
	rightResults := make(chan int)
	wg.Add(2)

	go competition(start, mid, leftResults, wg)
	go competition(mid+1, end, rightResults, wg)

	// Отримуємо переможців лівої та правої половини.
	leftWinner := <-leftResults
	rightWinner := <-rightResults

	// Проводимо фінальний бій між переможцями лівої та правої половини.
	winner := duel(leftWinner, rightWinner)
	results <- winner
}

func main() {
	rand.Seed(time.Now().UnixNano())

	// Ініціалізуємо масив з випадковими значеннями енергії Ци кожного ченця.
	const numChen = 8
	energy = make([]int, numChen)
	for i := range energy {
		energy[i] = rand.Intn(100)
	}

	fmt.Println("Енергія Ци ченців:", energy)

	// Запускаємо змагання та визначаємо переможця.
	var wg sync.WaitGroup
	results := make(chan int)
	wg.Add(1)
	go competition(0, numChen-1, results, &wg)

	winner := <-results
	fmt.Printf("Переможець: Ци ченец номер %d з енергією %d\n", winner, energy[winner])
}
