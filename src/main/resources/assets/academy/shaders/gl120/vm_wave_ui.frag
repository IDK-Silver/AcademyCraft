#version 120

uniform sampler2D tex;

varying float v_alpha;
varying vec2 v_uv;

void main() {
    gl_FragColor = texture2D(tex, v_uv) * v_alpha;
}
