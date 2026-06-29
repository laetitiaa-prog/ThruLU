package com.example.myapplication;



import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.List;
import java.util.Map;

/**
 * Custom View that renders the floor plan in isometric projection and
 * draws the Dijkstra shortest-path as an animated warm-amber line.
 *
 * Coordinate space:  SVG viewBox 0 0 760 560
 * Iso transform:     screenX = (svgX - svgY * 0.5) * IW
 *                    screenY = (svgX * 0.3 + svgY * 0.5) * IH
 */
public class IsometricFloorView extends View {

    // ── Isometric projection constants ─────────────────────────────────────
    private static final float IW   = 2.2f;   // horizontal stretch
    private static final float IH   = 1.1f;   // vertical stretch
    private static final float WALL = 18f;     // wall depth in SVG units

    // ── Scene ──────────────────────────────────────────────────────────────
    private List<Room> rooms;
    private Map<String, GraphNode> graph;

    // ── State ──────────────────────────────────────────────────────────────
    private Room selectedRoom    = null;
    private List<String> curPath = null;
    private float pathProgress   = 0f;   // 0..path.size-1

    // ── Pan / Zoom ─────────────────────────────────────────────────────────
    private float scaleFactor  = 1.0f;
    private float translateX   = 0f;
    private float translateY   = 0f;
    private float isoOffX, isoOffY;   // offset to keep iso map inside canvas

    private ScaleGestureDetector scaleDetector;
    private GestureDetector      panDetector;

    // ── Paints ─────────────────────────────────────────────────────────────
    // Hallway / floor
    private final Paint hallwayPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hallwayStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    // Room fills (earthy palette, no neon)
    private final Paint classTop    = makeFill(0xFF3E3028);
    private final Paint classTopHL  = makeFill(0xFF5a4838);
    private final Paint classSide   = makeFill(0xFF2A1E16);
    private final Paint classEdge   = makeStroke(0xFF6A5840, 0.8f);
    private final Paint officeTop   = makeFill(0xFF3A2C18);
    private final Paint officeTopHL = makeFill(0xFF56422A);
    private final Paint officeSide  = makeFill(0xFF281E10);
    private final Paint officeEdge  = makeStroke(0xFF7A6030, 0.8f);
    private final Paint adminTop    = makeFill(0xFF3C2E12);
    private final Paint adminTopHL  = makeFill(0xFF58441E);
    private final Paint adminSide   = makeFill(0xFF2A2008);
    private final Paint adminEdge   = makeStroke(0xFF887030, 0.8f);
    private final Paint amphiTop    = makeFill(0xFF3A2C1A);
    private final Paint amphiTopHL  = makeFill(0xFF564228);
    private final Paint amphiSide   = makeFill(0xFF281E10);
    private final Paint amphiEdge   = makeStroke(0xFF8A6035, 0.8f);
    private final Paint stairsTop   = makeFill(0xFF2C3024);
    private final Paint stairsSide  = makeFill(0xFF1E2018);
    private final Paint stairsEdge  = makeStroke(0xFF6A7A5A, 0.8f);
    private final Paint entrTop     = makeFill(0xFF302212);
    private final Paint entrSide    = makeFill(0xFF201608);
    private final Paint entrEdge    = makeStroke(0xFFA07840, 1f);
    // Target room override
    private final Paint targetTop   = makeFill(0xFF4A3520);
    private final Paint targetSide  = makeFill(0xFF342410);
    private final Paint targetEdge  = makeStroke(0xFFC8945A, 1.4f);
    // Labels
    private final Paint labelPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelHL     = new Paint(Paint.ANTI_ALIAS_FLAG);
    // Path line
    private final Paint pathPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // Door
    private final Paint doorPaint   = makeFill(0xFF1E1810);

    // ── Listeners ──────────────────────────────────────────────────────────
    public interface OnRoomClickListener {
        void onRoomClicked(Room room);
    }
    private OnRoomClickListener clickListener;

    // ── Animation ──────────────────────────────────────────────────────────
    private ValueAnimator pathAnimator;

    // ══════════════════════════════════════════════════════════════════════
    public IsometricFloorView(Context ctx) { this(ctx, null); }
    public IsometricFloorView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init(ctx);
    }

    private void init(Context ctx) {
        hallwayPaint.setColor(0xFF272018);
        hallwayStroke.setColor(0xFF362A1A);
        hallwayStroke.setStyle(Paint.Style.STROKE);
        hallwayStroke.setStrokeWidth(0.4f);

        labelPaint.setColor(0xFFA09070);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        labelHL.setColor(0xFFE8C890);
        labelHL.setTextAlign(Paint.Align.CENTER);

        pathPaint.setColor(0xFFC8945A);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
        pathPaint.setAntiAlias(true);

        markerPaint.setColor(0xFFE8A060);
        markerPaint.setAntiAlias(true);

        // Gesture detectors
        scaleDetector = new ScaleGestureDetector(ctx,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override public boolean onScale(ScaleGestureDetector d) {
                        scaleFactor = Math.max(0.3f, Math.min(4f, scaleFactor * d.getScaleFactor()));
                        invalidate();
                        return true;
                    }
                });

        panDetector = new GestureDetector(ctx,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
                        translateX -= dx;
                        translateY -= dy;
                        invalidate();
                        return true;
                    }
                    @Override public boolean onSingleTapUp(MotionEvent e) {
                        handleTap(e.getX(), e.getY());
                        return true;
                    }
                });
    }

    // ── Public API ─────────────────────────────────────────────────────────
    public void setData(List<Room> rooms, Map<String, GraphNode> graph) {
        this.rooms = rooms;
        this.graph = graph;
        recomputeOffset();
        invalidate();
    }

    public void setPath(List<String> path, Room dest) {
        this.selectedRoom = dest;
        this.curPath      = path;
        startPathAnimation();
    }

    public void setOnRoomClickListener(OnRoomClickListener l) { this.clickListener = l; }

    // ── Layout ─────────────────────────────────────────────────────────────
    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        recomputeOffset();
    }

    private void recomputeOffset() {
        // Find iso-space bounding box of the entire SVG
        float[] corners = {0,0, 760,0, 0,560, 760,560};
        float mnX=1e9f, mxX=-1e9f, mnY=1e9f, mxY=-1e9f;
        for (int i=0;i<8;i+=2) {
            PointF p = svgToIso(corners[i], corners[i+1]);
            if(p.x<mnX)mnX=p.x; if(p.x>mxX)mxX=p.x;
            if(p.y<mnY)mnY=p.y; if(p.y>mxY)mxY=p.y;
        }
        isoOffX = -mnX + 50;
        isoOffY = -mnY + 50;
        // Centre the map initially
        float mapW = (mxX - mnX + 100);
        float mapH = (mxY - mnY + 100);
        translateX = (getWidth()  - mapW * scaleFactor) / 2f;
        translateY = (getHeight() - mapH * scaleFactor) / 2f;
    }

    // ── Animation ──────────────────────────────────────────────────────────
    private void startPathAnimation() {
        if (pathAnimator != null) pathAnimator.cancel();
        pathProgress = 0;
        if (curPath == null || curPath.size() < 2) { invalidate(); return; }
        pathAnimator = ValueAnimator.ofFloat(0, curPath.size() - 1);
        pathAnimator.setDuration(Math.min(3000, curPath.size() * 180L));
        pathAnimator.setInterpolator(new DecelerateInterpolator());
        pathAnimator.addUpdateListener(a -> {
            pathProgress = (float) a.getAnimatedValue();
            invalidate();
        });
        pathAnimator.start();
    }

    // ── Draw ───────────────────────────────────────────────────────────────
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rooms == null) return;

        canvas.save();
        canvas.translate(translateX, translateY);
        canvas.scale(scaleFactor, scaleFactor);

        // Hallways
        float hw = 0xFF272018, hs = 0xFF362A1A;
        drawFlat(canvas, 8,   98, 744, 14);
        drawFlat(canvas,50,    8,  16,552);
        drawFlat(canvas,110,   8,  16, 88);
        drawFlat(canvas,110, 114,  16,220);
        drawFlat(canvas,218,   8,  10, 88);
        drawFlat(canvas,314,   8,  16, 88);
        drawFlat(canvas,314, 114,  16,220);
        drawFlat(canvas,432,   8,  16,552);

        // Doors
        drawFlatColoured(canvas, 8,  160, 98, 5, 0xFF1E1810);
        drawFlatColoured(canvas, 8,  266, 98, 5, 0xFF1E1810);
        drawFlatColoured(canvas,110,  112, 16, 5, 0xFF1E1810);
        drawFlatColoured(canvas,314,  112, 16, 5, 0xFF1E1810);
        drawFlatColoured(canvas,390,  250,116, 5, 0xFF1E1810);
        drawFlatColoured(canvas,432,  282, 58, 5, 0xFF1E1810);

        // Rooms
        boolean hasPath = curPath != null && curPath.size() > 1;
        for (Room r : rooms) {
            boolean isTarget   = selectedRoom != null && r.id.equals(selectedRoom.id);
            boolean inPath     = hasPath && isInPath(r.id);
            float   alpha      = hasPath ? (isTarget || inPath ? 1f : 0.42f) : 1f;
            drawRoom(canvas, r, isTarget, inPath, alpha);
        }

        // Path
        if (hasPath) drawPath(canvas);

        canvas.restore();
    }

    private boolean isInPath(String id) {
        if (curPath == null) return false;
        for (String s : curPath) if (s.equals(id)) return true;
        return false;
    }

    // ── Isometric box ──────────────────────────────────────────────────────
    private void drawRoom(Canvas canvas, Room r, boolean isTarget, boolean inPath, float alpha) {
        Paint top, topHL, side, edge;
        switch (r.type) {
            case OFFICE:   top=officeTop; topHL=officeTopHL; side=officeSide; edge=officeEdge; break;
            case ADMIN:    top=adminTop;  topHL=adminTopHL;  side=adminSide;  edge=adminEdge;  break;
            case AMPHI:    top=amphiTop;  topHL=amphiTopHL;  side=amphiSide;  edge=amphiEdge;  break;
            case STAIRS:   top=stairsTop; topHL=stairsTop;   side=stairsSide; edge=stairsEdge; break;
            case ENTRANCE: top=entrTop;   topHL=entrTop;      side=entrSide;   edge=entrEdge;   break;
            default:       top=classTop;  topHL=classTopHL;   side=classSide;  edge=classEdge;  break;
        }
        if (isTarget) { top=targetTop; topHL=targetTop; side=targetSide; edge=targetEdge; }

        float depth = WALL;
        // 4 corners of top face in iso screen space
        PointF TL=screenPt(r.svgX,       r.svgY);
        PointF TR=screenPt(r.svgX+r.svgW, r.svgY);
        PointF BR=screenPt(r.svgX+r.svgW, r.svgY+r.svgH);
        PointF BL=screenPt(r.svgX,        r.svgY+r.svgH);

        int al = Math.round(alpha * 255);

        // Left wall
        Path lw = quad(BL.x,BL.y, BL.x,BL.y+depth, BR.x,BR.y+depth, BR.x,BR.y);
        side.setAlpha(al); canvas.drawPath(lw, side);

        // Right wall
        Path rw = quad(TR.x,TR.y, TR.x,TR.y+depth, BR.x,BR.y+depth, BR.x,BR.y);
        Paint sideDim = new Paint(side); sideDim.setAlpha(Math.round(al*0.75f));
        canvas.drawPath(rw, sideDim);

        // Top face
        Path tp = quad(TL.x,TL.y, TR.x,TR.y, BR.x,BR.y, BL.x,BL.y);
        Paint useTop = (isTarget || inPath) ? topHL : top;
        useTop.setAlpha(al);
        canvas.drawPath(tp, useTop);

        Paint edgePaint = new Paint(edge); edgePaint.setAlpha(al);
        canvas.drawPath(tp, edgePaint);

        // Label
        if (r.svgW >= 18) {
            PointF centre = screenPt(r.cx(), r.cy());
            float  fs     = Math.max(6f, r.svgW < 50 ? 6f : 7f);
            Paint  lp     = isTarget ? labelHL : labelPaint;
            lp.setTextSize(fs); lp.setAlpha(al);
            canvas.drawText(r.label, centre.x, centre.y - 2, lp);
        }
    }

    // Flat quad (hallway / door)
    private void drawFlat(Canvas canvas, float x, float y, float w, float h) {
        Path p = isoQuad(x,y,w,h);
        canvas.drawPath(p, hallwayPaint);
        canvas.drawPath(p, hallwayStroke);
    }

    private void drawFlatColoured(Canvas canvas, float x, float y, float w, float h, int color) {
        Paint fp = new Paint(Paint.ANTI_ALIAS_FLAG); fp.setColor(color);
        canvas.drawPath(isoQuad(x,y,w,h), fp);
    }

    // ── Path drawing ───────────────────────────────────────────────────────
    private void drawPath(Canvas canvas) {
        if (curPath == null || curPath.size() < 2) return;
        int segs = curPath.size() - 1;
        float lift = 5f;

        for (int i = 0; i < segs; i++) {
            float prog = Math.max(0, Math.min(1, pathProgress - i));
            if (prog <= 0) break;

            GraphNode n1 = graph.get(curPath.get(i));
            GraphNode n2 = graph.get(curPath.get(i+1));
            if (n1==null||n2==null) continue;

            PointF p1 = screenPt(n1.x, n1.y);
            PointF p2 = screenPt(n2.x, n2.y);
            float  ex = p1.x + (p2.x - p1.x) * prog;
            float  ey = p1.y + (p2.y - p1.y) * prog;

            pathPaint.setStrokeWidth(2.4f);
            canvas.drawLine(p1.x, p1.y-lift, ex, ey-lift, pathPaint);

            // Leading dot
            Paint dot = new Paint(Paint.ANTI_ALIAS_FLAG);
            dot.setColor(0xFFE8C890);
            canvas.drawCircle(ex, ey-lift, 2.5f, dot);
        }

        // Start marker
        GraphNode start = graph.get(curPath.get(0));
        if (start != null) {
            PointF ps = screenPt(start.x, start.y);
            markerPaint.setColor(0xFFC89040);
            canvas.drawCircle(ps.x, ps.y - lift, 5.5f, markerPaint);
        }
        // End marker
        GraphNode end = graph.get(curPath.get(curPath.size()-1));
        if (end != null) {
            PointF pe = screenPt(end.x, end.y);
            markerPaint.setColor(0xFFE8A060);
            canvas.drawCircle(pe.x, pe.y - lift, 5.5f, markerPaint);
        }
    }

    // ── Touch ──────────────────────────────────────────────────────────────
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        panDetector.onTouchEvent(event);
        return true;
    }

    private void handleTap(float screenX, float screenY) {
        if (rooms == null || clickListener == null) return;
        // Convert screen → iso-space, then hit-test each room's top quad
        // We do inverse: test each room
        for (int i = rooms.size()-1; i >= 0; i--) {
            Room r = rooms.get(i);
            if (!r.isClickable()) continue;
            if (hitTestRoom(r, screenX, screenY)) {
                clickListener.onRoomClicked(r);
                return;
            }
        }
    }

    private boolean hitTestRoom(Room r, float sx, float sy) {
        // Back-transform screen point to iso space (accounting for pan/zoom)
        float ix = (sx - translateX) / scaleFactor;
        float iy = (sy - translateY) / scaleFactor;

        PointF TL=isoScreenPt(r.svgX,        r.svgY);
        PointF TR=isoScreenPt(r.svgX+r.svgW,  r.svgY);
        PointF BR=isoScreenPt(r.svgX+r.svgW,  r.svgY+r.svgH);
        PointF BL=isoScreenPt(r.svgX,         r.svgY+r.svgH);

        return pointInQuad(ix, iy, TL, TR, BR, BL);
    }

    /** ISO point without applying scale/translate (for hit testing after inversion). */
    private PointF isoScreenPt(float svgX, float svgY) {
        PointF p = svgToIso(svgX, svgY);
        return new PointF(p.x + isoOffX, p.y + isoOffY);
    }

    /** ISO point with full transform (for drawing). */
    private PointF screenPt(float svgX, float svgY) {
        return isoScreenPt(svgX, svgY);  // scale/translate applied via canvas.save/scale
    }

    // ── Geometry helpers ───────────────────────────────────────────────────
    private PointF svgToIso(float x, float y) {
        return new PointF((x - y*0.5f)*IW, (x*0.3f + y*0.5f)*IH);
    }

    private Path isoQuad(float x, float y, float w, float h) {
        PointF TL=screenPt(x,y), TR=screenPt(x+w,y), BR=screenPt(x+w,y+h), BL=screenPt(x,y+h);
        return quad(TL.x,TL.y, TR.x,TR.y, BR.x,BR.y, BL.x,BL.y);
    }

    private static Path quad(float x1,float y1,float x2,float y2,float x3,float y3,float x4,float y4){
        Path p=new Path();
        p.moveTo(x1,y1); p.lineTo(x2,y2); p.lineTo(x3,y3); p.lineTo(x4,y4); p.close();
        return p;
    }

    private static boolean pointInQuad(float px, float py, PointF a, PointF b, PointF c, PointF d) {
        PointF[] pts = {a, b, c, d};
        for (int i=0; i<4; i++) {
            PointF p1=pts[i], p2=pts[(i+1)%4];
            if ((p2.x-p1.x)*(py-p1.y) - (p2.y-p1.y)*(px-p1.x) < 0) return false;
        }
        return true;
    }

    private static Paint makeFill(int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color); p.setStyle(Paint.Style.FILL);
        return p;
    }

    private static Paint makeStroke(int color, float w) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(w);
        return p;
    }
}
