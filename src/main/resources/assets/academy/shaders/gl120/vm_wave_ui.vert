#version 120

uniform vec2 screenSize;

// per-vertex
attribute vec2 vertexPos;
attribute vec2 uv;

// per-instance (set via glVertexAttrib* on GL 2.1)
attribute vec2 offset;
attribute float size;
attribute float alpha;

varying float v_alpha;
varying vec2 v_uv;

void main() {
    vec2 pos = (vertexPos * size) + offset;

    gl_Position = vec4(pos / screenSize - vec2(0.5), 0, 1);

    v_alpha = alpha;
    v_uv = uv;
}
