import sounddevice as sd
import sys

import numpy as np


loudest = 0

def print_sound(indata, outdata, frames, time, status):
    volume_norm = np.linalg.norm(indata)*100
    volume_norm //= 1
    global loudest
    if(volume_norm > loudest):
        loudest = volume_norm

with sd.Stream(callback=print_sound):
    sd.sleep(200)

f = open("pngassets/audio_level.txt", "w")
f.write(str(loudest))
print(loudest)
sys.stdout.flush()
