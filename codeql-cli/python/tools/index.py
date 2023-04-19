#!/usr/bin/env python

#This file needs to be able to handle all versions of Python we are likely to encounter
#Which is probably 3.0 and upwards

'''Run index.py in buildtools'''

import os
import sys
from python_tracer import getzipfilename

if 'SEMMLE_DIST' in os.environ:
    if 'CODEQL_EXTRACTOR_PYTHON_ROOT' not in os.environ:
        os.environ['CODEQL_EXTRACTOR_PYTHON_ROOT'] = os.environ['SEMMLE_DIST']
else:
    os.environ["SEMMLE_DIST"] = os.environ["CODEQL_EXTRACTOR_PYTHON_ROOT"]

tools = os.path.join(os.environ['SEMMLE_DIST'], "tools")
zippath = os.path.join(tools, getzipfilename())
sys.path = [ zippath ] + sys.path

import buildtools.index
buildtools.index.main()
