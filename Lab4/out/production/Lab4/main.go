package main

import (
	"fmt"
	"sync"
	"time"
)

type BusRoute struct {
	fromCity string
	toCity   string
	price    int
}

type BusGraph struct {
	routes map[string]map[string]int
	mu     sync.RWMutex
}

func (g *BusGraph) AddRoute(fromCity, toCity string, price int) {
	g.mu.Lock()
	defer g.mu.Unlock()

	if g.routes[fromCity] == nil {
		g.routes[fromCity] = make(map[string]int)
	}
	if g.routes[toCity] == nil {
		g.routes[toCity] = make(map[string]int)
	}

	g.routes[fromCity][toCity] = price
	g.routes[toCity][fromCity] = price
}

func (g *BusGraph) RemoveRoute(fromCity, toCity string) {
	g.mu.Lock()
	defer g.mu.Unlock()

	delete(g.routes[fromCity], toCity)
	delete(g.routes[toCity], fromCity)
}

func (g *BusGraph) AddCity(city string) {
	g.mu.Lock()
	defer g.mu.Unlock()

	if g.routes[city] == nil {
		g.routes[city] = make(map[string]int)
	}
}

func (g *BusGraph) RemoveCity(city string) {
	g.mu.Lock()
	defer g.mu.Unlock()

	delete(g.routes, city)
	for _, cities := range g.routes {
		delete(cities, city)
	}
}

func (g *BusGraph) FindPath(fromCity, toCity string) (bool, int) {
	g.mu.RLock()
	defer g.mu.RUnlock()

	visited := make(map[string]bool)
	queue := []struct {
		city  string
		price int
	}{{
		city:  fromCity,
		price: 0,
	}}

	for len(queue) > 0 {
		current := queue[0]
		queue = queue[1:]

		if current.city == toCity {
			return true, current.price
		}

		visited[current.city] = true

		for city, price := range g.routes[current.city] {
			if !visited[city] {
				queue = append(queue, struct {
					city  string
					price int
				}{city, current.price + price})
			}
		}
	}

	return false, 0
}

func main() {
	busGraph := &BusGraph{
		routes: make(map[string]map[string]int),
	}

	busGraph.AddRoute("CityA", "CityB", 45)
	busGraph.AddRoute("CityA", "CityC", 30)
	busGraph.AddRoute("CityB", "CityC", 20)
	busGraph.AddCity("CityD")

	priceChangeSequence := []int{45, 60, 75, 90} // Ваша послідовність цін

	go func() {
		for i := 0; ; i = (i + 1) % len(priceChangeSequence) {
			time.Sleep(5 * time.Second)
			newPrice := priceChangeSequence[i]
			busGraph.mu.Lock()
			busGraph.routes["CityA"]["CityB"] = newPrice
			busGraph.routes["CityB"]["CityA"] = newPrice
			busGraph.mu.Unlock()
			fmt.Printf("Змінено ціну квитка на CityA - CityB: %d\n", newPrice)
			printGraph(busGraph)
		}
	}()

	go func() {
		for {
			time.Sleep(10 * time.Second)
			busGraph.mu.Lock()
			delete(busGraph.routes["CityA"], "CityC")
			busGraph.routes["CityA"]["CityD"] = 40
			busGraph.mu.Unlock()
			fmt.Println("Видалено рейс CityA - CityC і додано рейс CityA - CityD")
			printGraph(busGraph)
		}
	}()

	go func() {
		for {
			time.Sleep(15 * time.Second)
			busGraph.mu.Lock()
			delete(busGraph.routes, "CityB")
			busGraph.mu.Unlock()
			fmt.Println("Видалено місто CityB")
			printGraph(busGraph)
		}
	}()

	for {
		time.Sleep(10 * time.Second)
	}
}

func printGraph(graph *BusGraph) {
	fmt.Println("Граф автобусних рейсів:")
	for fromCity, routes := range graph.routes {
		for toCity, price := range routes {
			fmt.Printf("%s -> %s: %d\n", fromCity, toCity, price)
		}
	}
	fmt.Println("-------------------")
}
