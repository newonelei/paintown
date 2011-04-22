#include "util/bitmap.h"
#include "collector.h"
#include "paintown-engine/factory/heart_factory.h"
#include "nameplacer.h"
#include "font_factory.h"
#include "font_render.h"
#include "paintown-engine/factory/object_factory.h"
#include "util/resource.h"
#include "mugen/config.h"
#include "util/sound.h"
#include <iostream>

using namespace std;

#ifndef debugx
#define debugx cout<<"File: "<<__FILE__<<" Line: "<<__LINE__<<endl;
#endif

Collector::Collector(){
    /* the only resource */
    resource = new Resource();
}

/* cleans up global state */
Collector::~Collector(){
    ObjectFactory::destroy();
    NamePlacer::destroy();
    HeartFactory::destroy();
    FontFactory::destroy();
    FontRender::destroy();
    Graphics::Bitmap::cleanupTemporaryBitmaps();
    Graphics::Bitmap::shutdown();
    Mugen::Data::destroy();
    Sound::uninitialize();
    delete resource;
}
