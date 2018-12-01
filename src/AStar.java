import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStar implements PathingStrategy {

    //Start - Node Definition and its functions-------------------------------------------------------------------------
    public class node{
        private Point point;
        private int fromStart;
        private int toEnd;
        private int totDist;
        private node previous;

        node(Point current, Point start, Point end){
            point = current;
            fromStart = Math.abs(current.x - start.x) + Math.abs(current.y - start.y);
            //toEnd= Math.abs(current.x - end.x) + Math.abs(current.y - end.y);
            toEnd= Math.abs(end.x - current.x) + Math.abs(end.y - current.y);
            totDist = fromStart + toEnd;
            previous = null;
        }

        public void setPoint(Point inPoint){point = inPoint;}
        public void setPrevious(node inNode){previous = inNode;}
        public void setdFromStart(int inStart){fromStart = inStart;}
        public void setHeuDist(int inEnd){ toEnd = inEnd;}
        public void setTotDist(int inTotDist){totDist = inTotDist;}

        public Point getPoint(){return point;}
        public node getPrevious(){return previous;}
        public int getFromStart(){return fromStart;}
        public int getToEnd(){return toEnd;}
        public int getTotDist(){return totDist;}
    }
//End - Node definition and its functions-------------------------------------------------------------------------------




//Helper functions: findSmallest in list according to totDist and pointToNode

    public node findSmallest(List<node>inList, List<node> inOpen, List<node> inClose){
        node smallest = inList.get(0);
        for(int i = 0; i < inList.size(); i++){
            if (smallest.totDist > inList.get(i).totDist && !inOpen.contains(smallest) && !inClose.contains(smallest))
            {
                smallest = inList.get(i);
            }
        }

        return  smallest;
    }

    public List<node> pointToNode(List<Point> inPoints, Point start, Point end ){
        List<node> convert = new LinkedList<>();
        for(Point p : inPoints){
            convert.add(new node(p, start, end));
        }
        return convert;
    }


    List<node> open = new LinkedList<>();
    List<node> close = new LinkedList<>();
    List<node> allNeig = new LinkedList<>();


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        /* Does not check withinReach.  Since only a single step is taken
         * on each call, the caller will need to check if the destination
         * has been reached.
         */

        List<Point> aux = new LinkedList<>();

        if(open == null || open.isEmpty() || allNeig.isEmpty()){
            node startNode = new node(start, start, end);
            open.add(startNode);
            List<Point> neighbors = potentialNeighbors.apply(start)
                    .filter(canPassThrough)
                    .filter(pt -> !pt.equals(start) && !pt.equals(end) //&& withinBounds(pt))
                            && Math.abs(end.x - pt.x) <= Math.abs(end.x - start.x)
                            && Math.abs(end.y - pt.y) <= Math.abs(end.y - start.y)
                            )
                    .limit(4)
                    .collect(Collectors.toList());

            List<node> neighborNodeForm = pointToNode(neighbors, start, end);
            for(node n : neighborNodeForm){
                n.setPrevious(startNode);
            }

            for(node n : neighborNodeForm){
                allNeig.add(n);
            }
            close.add(startNode);

        }

        else{

            aux.clear();
            node tempNode = findSmallest(allNeig, open, close);
            allNeig.remove(tempNode);

            List<Point> allNeigPoint = new ArrayList<>();
            for(node n : allNeig){
                allNeigPoint.add(n.point);
            }

            open.add(tempNode);

            List<Point> neighbors = potentialNeighbors.apply(tempNode.point)
                    .filter(canPassThrough)
                    .filter(pt -> !pt.equals(start) && !pt.equals(end)
                            && !allNeigPoint.contains(pt)
                            && !close.contains(pt)
                            && !open.contains(pt))
                    .limit(4)
                    .collect(Collectors.toList());


            List<node> neighborNodeForm = pointToNode(neighbors, start, end);
            for(node n : neighborNodeForm){
                n.setPrevious(open.get(open.size() - 1));
            }

            allNeig.addAll(neighborNodeForm);

            close.add(tempNode);

            aux.add(tempNode.point);
        }
        return aux;
    }

}
