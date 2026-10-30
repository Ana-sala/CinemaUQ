"""Genera diagramas UML de CinemaUQ con Graphviz."""
import html, subprocess

FONT = "Helvetica"

def clase(nombre, attrs=(), metodos=(), estereotipo=None, abstracta=False, color="#ffffff", borde="#1f2a44"):
    e = lambda s: html.escape(s)
    titulo = ""
    if estereotipo:
        titulo += f'<FONT POINT-SIZE="9">«{e(estereotipo)}»</FONT><BR/>'
    titulo += f'<I><B>{e(nombre)}</B></I>' if abstracta else f'<B>{e(nombre)}</B>'
    filas = f'<TR><TD BGCOLOR="{color}" ALIGN="CENTER">{titulo}</TD></TR>'
    def bloque(items):
        if not items:
            return '<TR><TD ALIGN="LEFT"> </TD></TR>'
        lineas = []
        for it in items:
            t = e(it)
            if it.endswith("{abstract}"):
                t = f'<I>{e(it.replace(" {abstract}", ""))}</I>'
            if it.endswith("{static}"):
                t = f'<U>{e(it.replace(" {static}", ""))}</U>'
            lineas.append(t)
        return '<TR><TD ALIGN="LEFT" BALIGN="LEFT">' + '<BR/>'.join(lineas) + '</TD></TR>'
    if estereotipo == "enumeration":
        filas += bloque(attrs)
    else:
        filas += bloque(attrs) + bloque(metodos)
    return (f'<<TABLE BORDER="1" CELLBORDER="1" CELLSPACING="0" CELLPADDING="4" COLOR="{borde}">{filas}</TABLE>>')

class Diagrama:
    def __init__(self, titulo, rankdir="TB", nodesep=0.5, ranksep=0.7, splines="ortho"):
        self.l = [f'digraph G {{',
                  f'graph [fontname="{FONT}", fontsize=20, labelloc=t, label="{titulo}", rankdir={rankdir}, '
                  f'nodesep={nodesep}, ranksep={ranksep}, splines={splines}, newrank=true, pad=0.4];',
                  f'node [shape=plaintext, fontname="{FONT}", fontsize=10];',
                  f'edge [fontname="{FONT}", fontsize=9, color="#3b4252"];']
    def nodo(self, id_, label):
        self.l.append(f'{id_} [label={label}];')
    def raw(self, s):
        self.l.append(s)
    # relaciones UML (la flecha va de hijo a padre / de todo a parte)
    def hereda(self, hijo, padre):
        self.l.append(f'{padre} -> {hijo} [dir=back, arrowtail=onormal, arrowsize=1.3];')
    def implementa(self, clase_, interfaz):
        self.l.append(f'{interfaz} -> {clase_} [dir=back, arrowtail=onormal, style=dashed, arrowsize=1.3];')
    def compone(self, todo, parte, mult_parte="", mult_todo="1", etiqueta=""):
        self.l.append(f'{todo} -> {parte} [dir=both, arrowtail=diamond, arrowhead=none, headlabel="{mult_parte}", '
                      f'taillabel="{mult_todo}", xlabel="{etiqueta}", labeldistance=1.6];')
    def agrega(self, todo, parte, mult_parte="", mult_todo="", etiqueta=""):
        self.l.append(f'{todo} -> {parte} [dir=both, arrowtail=odiamond, arrowhead=none, headlabel="{mult_parte}", '
                      f'taillabel="{mult_todo}", xlabel="{etiqueta}", labeldistance=1.6];')
    def asocia(self, origen, destino, mult_destino="", mult_origen="", etiqueta=""):
        self.l.append(f'{origen} -> {destino} [arrowhead=vee, headlabel="{mult_destino}", taillabel="{mult_origen}", '
                      f'xlabel="{etiqueta}", labeldistance=1.6];')
    def usa(self, origen, destino, etiqueta=""):
        self.l.append(f'{origen} -> {destino} [arrowhead=vee, style=dashed, xlabel="{etiqueta}"];')
    def nota(self, id_, texto, unido_a=None):
        self.l.append(f'{id_} [shape=note, style=filled, fillcolor="#fff8dc", fontsize=9, label="{texto}"];')
        if unido_a:
            self.l.append(f'{id_} -> {unido_a} [style=dotted, arrowhead=none];')
    def guardar(self, nombre, formatos=("pdf", "png")):
        self.l.append("}")
        open(f"{nombre}.dot", "w").write("\n".join(self.l))
        for f in formatos:
            extra = ["-Gdpi=170"] if f == "png" else []
            subprocess.run(["dot", f"-T{f}", *extra, f"{nombre}.dot", "-o", f"{nombre}.{f}"], check=True)
