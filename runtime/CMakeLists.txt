cmake_minimum_required(VERSION 3.3)
project(wich_runtime)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES src/wich.c src/wich.h src/memory.c src/gen_runs0.c)
add_executable(wich_runtime ${SOURCE_FILES})