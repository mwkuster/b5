def quicksort (liste)
  if liste.nil? or liste.length <= 1 then
    liste
  else
    pivot = liste[0]
    kleiner, groessergleich = liste.partition { |z| z < pivot }
    quicksort(kleiner) + [pivot] + quicksort(groessergleich[1..-1])
  end
end
