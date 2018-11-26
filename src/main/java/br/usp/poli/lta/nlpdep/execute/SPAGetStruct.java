package br.usp.poli.lta.nlpdep.execute;

import br.usp.poli.lta.nlpdep.mwirth2ape.ape.conversion.Sketch;
import br.usp.poli.lta.nlpdep.mwirth2ape.exporter.Transition;
import br.usp.poli.lta.nlpdep.mwirth2ape.exporter.Spec;
import br.usp.poli.lta.nlpdep.mwirth2ape.labeling.LabelElement;

import java.util.*;

public class SPAGetStruct {

    private final List<Sketch> transitions;

    public SPAGetStruct(List<Sketch> transitions) {
        this.transitions = transitions;
    }

    public Map<String, List<Sketch>> getMachinesFromTransitions ()
    {
        Map<String, List<Sketch>> map = new HashMap<>();
        transitions.stream().map((sketch) -> {
            if (!map.containsKey(sketch.getName())) {
                map.put(sketch.getName(), new ArrayList<>());
            }
            return sketch;
        }).forEach((sketch) -> {

            map.get(sketch.getName()).add(sketch);
        });
        return map;
    }


    public Map<String, Integer> getStateQtyFromMachineMap(Map <String, List<Sketch>> machineMap)
    {
        Map<String, Integer> map = new HashMap<>();
        for (String tempMachine: machineMap.keySet()) {
            HashSet<Integer> states = new HashSet<>();
            Integer stateQty = 0;
            //List<Sketch> tempSketches = machineMap.get(tempMachine); // Get transitions for a machine
            for (Sketch tempSketch: machineMap.get(tempMachine)) {
                if (!states.contains(tempSketch.getSource())) {
                    states.add(tempSketch.getSource());
                }
                if (!states.contains(tempSketch.getTarget())) {
                    states.add(tempSketch.getTarget());
                }
            }
            map.put(tempMachine,states.size());
        }
        return map;
    }

    public Map<String, Integer> getMaxStateIdFromMachineMap(Map <String, List<Sketch>> machineMap)
    {
        Integer maxId = 0;
        Map<String, Integer> map = new HashMap<>();
        for (String tempMachine: machineMap.keySet()) {
            HashSet<Integer> states = new HashSet<>();
            Integer stateQty = 0;
            //List<Sketch> tempSketches = machineMap.get(tempMachine); // Get transitions for a machine
            for (Sketch tempSketch: machineMap.get(tempMachine)) {
                if (!states.contains(tempSketch.getSource())) {
                    states.add(tempSketch.getSource());
                }
                if (!states.contains(tempSketch.getTarget())) {
                    states.add(tempSketch.getTarget());
                }
            }
            map.put(tempMachine,Collections.max(states));
        }
        return map;
    }

    public Map<String, Spec> generateMachineMap()
            throws Exception {
        Map<String, List<Sketch>> map = getMachinesFromTransitions();

        Map<String, Spec> result = new HashMap<>();
        for (String machine : map.keySet()) {
            result.put(machine, buildSpec(machine, map.get(machine)));
        }
        return result;
    }

    public Spec buildSpec(String name, List<Sketch> sketches)
            throws Exception {
        Spec spec = new Spec();
        spec.setName(name);
        spec.setInitial(0);
        spec.setAccepting(Arrays.asList(1));
        List<Transition> ts = new ArrayList<>();
        sketches.stream().map((sketch) -> {
            Transition t = new Transition();
            t.setFrom(sketch.getSource());
            t.setTo(sketch.getTarget());
            if (!sketch.epsilon()) {
                if (sketch.call()) {
                    t.setSymbol(sketch.getSubmachine().concat(" (call)"));
                }
                else {
                    t.setSymbol(sketch.getToken().getValue());
                }
            }

            if (sketch.getToken() != null) {
                if (sketch.getToken().getProductionToken() != null) {
                    if (sketch.getToken().getProductionToken().getLabels() != null) {
                        LinkedList<String> newLabels = new LinkedList<>();
                        for (LabelElement tempLabel : sketch.getToken().getProductionToken().getLabels()) {
                            newLabels.push(tempLabel.getValue());
                        }
                        t.setLabels(newLabels);
                    }
                }
            }

            return t;
        }).forEach((t) -> {
            ts.add(t);
        });
        spec.setTransitions(ts);
        return spec;
    }

}
