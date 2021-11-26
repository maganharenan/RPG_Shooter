package com.maganharenan.world;

import java.util.*;

public class AStar {
    private static double lastTime = System.currentTimeMillis();
    private static Comparator<Node> nodeSorter = new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) {
            if (o2.fCost < o1.fCost) {
                return  +1;
            }
            if (o2.fCost > o1.fCost) {
                return -1;
            }

            return 0;
        }
    };

    public static boolean clear() {
        if (System.currentTimeMillis() - lastTime >= 1000) {
            return true;
        }

        return false;
    }

    public static List<Node> findPath(World world, Vector2i start, Vector2i end) {
        lastTime = System.currentTimeMillis();
        List<Node> openList = new ArrayList<Node>();
        List<Node> closedList = new ArrayList<Node>();

        Node currentNode = new Node(start, null, 0, getDistance(start, end));

        openList.add(currentNode);

        while (openList.size() > 0) {
            Collections.sort(openList, nodeSorter);
            currentNode = openList.get(0);
            if (currentNode.tile.equals(end)) {
                List<Node> path = new ArrayList<Node>();
                while (currentNode.parent != null) {
                    path.add(currentNode);
                    currentNode = currentNode.parent;
                }
                openList.clear();
                closedList.clear();
                return path;
            }

            openList.remove(currentNode);
            closedList.add(currentNode);

            for (int index = 0; index < 9; index++) {
                if (index == 4) {
                    continue;
                }
                int x = currentNode.tile.x;
                int y = currentNode.tile.y;
                int xIndex = (index%3) - 1;
                int yIndex = (index/3) - 1;
                Tile tile = World.tiles[x+xIndex+((y+yIndex)*World.WIDTH)];

                if (tile == null) {
                    continue;
                }
                if (tile instanceof WallTile) {
                    continue;
                }
                if (index == 0) {
                    Tile testTile = World.tiles[x+xIndex+1+((y+yIndex)*World.WIDTH)];
                    Tile testTile2 = World.tiles[x+xIndex+1+((y+yIndex)*World.WIDTH)];

                    if (testTile instanceof WallTile || testTile2 instanceof  WallTile) {
                        continue;
                    }
                }
                else if (index == 2) {
                    Tile testTile = World.tiles[x+xIndex+1+((y+yIndex)*World.WIDTH)];
                    Tile testTile2 = World.tiles[x+xIndex+((y+yIndex)*World.WIDTH)];

                    if (testTile instanceof WallTile || testTile2 instanceof  WallTile) {
                        continue;
                    }
                }
                else if (index == 6) {
                    Tile testTile = World.tiles[x+xIndex+((y+yIndex)*World.WIDTH)];
                    Tile testTile2 = World.tiles[x+xIndex+1+((y+yIndex)*World.WIDTH)];

                    if (testTile instanceof WallTile || testTile2 instanceof  WallTile) {
                        continue;
                    }
                }
                else if (index == 8) {
                    Tile testTile = World.tiles[x+xIndex+((y+yIndex)*World.WIDTH)];
                    Tile testTile2 = World.tiles[x+xIndex-1+((y+yIndex)*World.WIDTH)];

                    if (testTile instanceof WallTile || testTile2 instanceof  WallTile) {
                        continue;
                    }
                }

                Vector2i vectorA = new Vector2i(x+xIndex, y+yIndex);
                double gCost = currentNode.gCost + getDistance(currentNode.tile, vectorA);
                double hCost = getDistance(vectorA, end);

                Node node = new Node(vectorA, currentNode, gCost, hCost);

                if (vectorAlreadyExistInList(closedList, vectorA) && gCost >= currentNode.gCost) {
                    continue;
                }

                if (!vectorAlreadyExistInList(openList, vectorA)) {
                    openList.add(node);
                }
                else if (gCost < currentNode.gCost) {
                    openList.remove(currentNode);
                    openList.add(node);
                }
            }
        }

        closedList.clear();
        return null;
    }

    private static boolean vectorAlreadyExistInList(List<Node> list, Vector2i vector) {
        for (int index = 0; index < list.size(); index++) {
            if (list.get(index).tile.equals(vector)) {
                return true;
            }
        }

        return false;
    }

    private static double getDistance(Vector2i tile, Vector2i goal) {
        double distanceX = tile.x - goal.x;
        double distanceY = tile.y - goal.y;

        return Math.sqrt(distanceX * distanceX + distanceY * distanceY);
    }
}
