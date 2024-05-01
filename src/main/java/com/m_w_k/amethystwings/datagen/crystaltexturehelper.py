import numpy as np
from PIL import Image

imgbase = Image.open('base.png')
edgeLength = imgbase.width
frameCount = int(imgbase.height / edgeLength)  # this should always be an integer multiple
superarray = []
for i in range(0, frameCount):
    crop = imgbase.crop((0, edgeLength * i, edgeLength, edgeLength * (i + 1)))
    colors = crop.getcolors()

    array = np.random.randint(
        low=0,
        high=len(colors) - 1,
        size=(edgeLength, edgeLength)
    )

    array = np.array([[colors[x][1] for x in row] for row in array])
    if (len(superarray) == 0):
        superarray = array
    else:
        superarray = np.concatenate((superarray, array))

img = Image.fromarray(np.uint8(superarray.astype('uint8')))

img.save('generatedcrystaltexture.png')
